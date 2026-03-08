package com.phonenexus.sales.services.impl;

import com.phonenexus.sales.clients.ProductClient;
import com.phonenexus.sales.exception.ResourceNotFoundException;
import com.phonenexus.sales.models.*;
import com.phonenexus.sales.payload.request.OrderRequest;
import com.phonenexus.sales.payload.response.OrderItemResponse;
import com.phonenexus.sales.payload.response.OrderResponse;
import com.phonenexus.sales.repositories.CartRepository;
import com.phonenexus.sales.repositories.OrderRepository;
import com.phonenexus.sales.repositories.OrderStatusHistoryRepository;
import com.phonenexus.sales.repositories.PaymentTransactionRepository;
import com.phonenexus.sales.services.CartService;
import com.phonenexus.sales.services.OrderService;
import com.phonenexus.sales.events.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("30000");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Autowired
    private PaymentTransactionRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private com.phonenexus.sales.clients.UserClient userClient;

    @Autowired
    private com.phonenexus.sales.repositories.PromotionRepository promotionRepository;

    @Override
    @Transactional
    public OrderResponse checkout(String userId, OrderRequest request) {
        // 0. IDEMPOTENCY CHECK: Check for recent orders (last 60s) to avoid duplicates
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(60);
        List<Order> recentOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!recentOrders.isEmpty()) {
            Order latest = recentOrders.get(0);
            if (latest.getCreatedAt().isAfter(threshold) &&
                    latest.getStatus() == OrderStatus.PENDING &&
                    latest.getShippingAddress().equals(request.getShippingAddress())) {
                throw new RuntimeException("Error: Duplicate order detected. Please wait a minute.");
            }
        }

        // 1. Get active cart
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Empty cart or active cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Error: Cart is empty.");
        }

        // 2. Financials will be calculated in the loop for Fresh Pricing
        // 3. Create Order Shell (Total will be updated after pricing loop)
        Order order = Order.builder()
                .userId(userId)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .build();

        // 4. Convert CartItems to OrderItems with FRESH PRICING and stock rollback
        List<CartItem> processedItems = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        try {
            for (CartItem cartItem : cart.getItems()) {
                // FRESH PRICING: Fetch latest price from Product microservice
                var freshProduct = productClient.getProductById(cartItem.getProductId());
                var freshVariant = freshProduct.getVariants().stream()
                        .filter(v -> v.getId().equals(cartItem.getVariantId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Variant no longer exists: " + cartItem.getVariantId()));

                BigDecimal currentPrice = freshVariant.getPrice();
                subTotal = subTotal.add(currentPrice.multiply(new BigDecimal(cartItem.getQuantity())));

                // Fetch available IMEIs for this variant
                List<com.phonenexus.sales.dto.external.ProductItemExternalResponse> availableItems = productClient
                        .getAvailableItems(cartItem.getVariantId(), "INTERNAL-SERVICE-TOKEN-2026", "ADMIN");

                if (availableItems.size() < cartItem.getQuantity()) {
                    throw new RuntimeException(
                            "Not enough available items with IMEI for variant: " + freshVariant.getSku());
                }

                // Create individual OrderItems for each physical unit (IMEI)
                for (int i = 0; i < cartItem.getQuantity(); i++) {
                    com.phonenexus.sales.dto.external.ProductItemExternalResponse itemImei = availableItems.get(i);
                    OrderItem orderItem = OrderItem.builder()
                            .order(order)
                            .productId(cartItem.getProductId())
                            .variantId(cartItem.getVariantId())
                            .productName(cartItem.getProductName())
                            .sku(freshVariant.getSku())
                            .price(currentPrice)
                            .costPrice(itemImei.getCostPrice()) // Use specific cost price from IMEI
                            .quantity(1) // 1 unit per IMEI
                            .imageUrl(cartItem.getImageUrl())
                            .imei(itemImei.getImei())
                            .build();
                    order.addItem(orderItem);
                }

                // Reduce stock via Feign
                productClient.reduceStock(cartItem.getVariantId(), cartItem.getQuantity(),
                        "INTERNAL-SERVICE-TOKEN-2026");
                processedItems.add(cartItem);
            }
        } catch (Exception e) {
            // MANUAL ROLLBACK: Increase stock for items that were already reduced
            for (CartItem item : processedItems) {
                try {
                    productClient.increaseStock(item.getVariantId(), item.getQuantity(), "INTERNAL-SERVICE-TOKEN-2026");
                } catch (Exception ex) {
                    log.error("CRITICAL: Failed to rollback stock for variant: {} during checkout of user {}",
                            item.getVariantId(), userId, ex);
                }
            }
            throw new RuntimeException("Checkout failed during stock reduction: " + e.getMessage());
        }

        // 5. Finalize Order Totals & Loyalty Points
        BigDecimal shippingFee = DEFAULT_SHIPPING_FEE;
        BigDecimal taxAmount = subTotal.multiply(new BigDecimal("0.1"));
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Apply Promotion if provided
        if (request.getPromotionCode() != null && !request.getPromotionCode().isBlank()) {
            var promotion = promotionRepository.findByCode(request.getPromotionCode())
                    .orElseThrow(() -> new RuntimeException("Error: Invalid promotion code."));

            if (promotion.getStartDate().isAfter(LocalDateTime.now()) ||
                    promotion.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Error: Promotion code expired.");
            }

            if (promotion.getUsageLimit() != null && promotion.getUsedCount() >= promotion.getUsageLimit()) {
                throw new RuntimeException("Error: Promotion code usage limit reached.");
            }

            if (subTotal.compareTo(promotion.getMinimumOrderAmount()) < 0) {
                throw new RuntimeException("Error: Order total does not meet minimum requirement for this promotion: "
                        + promotion.getMinimumOrderAmount());
            }

            if (promotion.isPercentage()) {
                discountAmount = subTotal.multiply(promotion.getDiscountAmount().divide(new BigDecimal("100")));
            } else {
                discountAmount = promotion.getDiscountAmount();
            }
            // Cap discount to subtotal
            if (discountAmount.compareTo(subTotal) > 0) {
                discountAmount = subTotal;
            }

            // Update usage count
            promotion.setUsedCount(promotion.getUsedCount() + 1);
            promotionRepository.save(promotion);
        }

        order.setTotalAmount(subTotal.add(shippingFee).add(taxAmount).subtract(discountAmount));
        order.setShippingFee(shippingFee);
        order.setTaxAmount(taxAmount);
        order.setDiscountAmount(discountAmount);

        // Calculate Loyalty Points: 1,000,000 VND = 10 pts (100k = 1pt)
        try {
            var user = userClient.getUserById(UUID.fromString(userId));
            double multiplier = switch (user.getMembershipTier()) {
                case "SILVER" -> 1.1;
                case "GOLD" -> 1.2;
                case "DIAMOND" -> 1.5;
                default -> 1.0;
            };
            // Points = (Total / 100,000) * multiplier
            int basePoints = subTotal.divide(new BigDecimal("100000"), 0, java.math.RoundingMode.DOWN).intValue();
            int finalPoints = (int) (basePoints * multiplier);
            order.setLoyaltyPoints(finalPoints);
        } catch (Exception e) {
            log.error("Failed to calculate loyalty points for user {}: {}", userId, e.getMessage());
            order.setLoyaltyPoints(0);
        }

        // Log history
        historyRepository.save(com.phonenexus.sales.models.OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.PENDING)
                .note("Order created via checkout.")
                .build());

        // 6. Create Payment Transaction (Mock for now)
        paymentRepository.save(com.phonenexus.sales.models.PaymentTransaction.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .provider(order.getPaymentMethod())
                .status(com.phonenexus.sales.models.PaymentStatus.PENDING)
                .transactionId("MOCK-" + UUID.randomUUID().toString())
                .build());

        // 7. Clear Cart
        cartService.clearCart(userId);

        return mapToResponse(order);
    }

    @Override
    public OrderResponse getOrderDetails(UUID orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        checkOwnership(userId, order);
        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> getOrderHistory(String userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, String status, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        checkOwnership(userId, order);

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Invalid order status: " + status);
        }

        if (oldStatus != newStatus) {
            // STATE MACHINE CHECK
            boolean isValid = switch (oldStatus) {
                case PENDING ->
                    List.of(OrderStatus.PAID, OrderStatus.CANCELLED, OrderStatus.SHIPPED).contains(newStatus);
                case PAID -> List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED).contains(newStatus);
                case SHIPPED -> List.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED).contains(newStatus);
                case COMPLETED -> List.of(OrderStatus.RETURNING).contains(newStatus);
                case RETURNING -> List.of(OrderStatus.RETURNED, OrderStatus.COMPLETED).contains(newStatus);
                default -> false;
            };

            if (!isValid) {
                throw new RuntimeException("Error: Illegal status transition from " + oldStatus + " to " + newStatus);
            }

            // Logic for FINALIZING RETURN
            if (newStatus == OrderStatus.RETURNED) {
                // Restore stock and IMEI status
                for (OrderItem item : order.getItems()) {
                    productClient.increaseStock(item.getVariantId(), item.getQuantity(), "INTERNAL-SERVICE-TOKEN-2026");
                    if (item.getImei() != null) {
                        productClient.updateItemStatusByImei(item.getImei(), "AVAILABLE",
                                "INTERNAL-SERVICE-TOKEN-2026");
                    }
                }
                // Update payment status if exists
                paymentRepository.findByOrderId(orderId).ifPresent(tx -> {
                    tx.setStatus(com.phonenexus.sales.models.PaymentStatus.REFUNDED);
                    // paymentRepository.save(tx); -> Managed
                });
            }

            order.setStatus(newStatus);
            // order = orderRepository.save(order); -> Managed

            // 1. Log history
            historyRepository.save(com.phonenexus.sales.models.OrderStatusHistory.builder()
                    .order(order)
                    .status(newStatus)
                    .note("Status updated from " + oldStatus + " to " + newStatus)
                    .build());

            // 2. Publish Notification Event (for SHIPPED, COMPLETED, RETURNED)
            if (List.of(OrderStatus.SHIPPED, OrderStatus.COMPLETED, OrderStatus.RETURNED).contains(newStatus)) {
                publishOrderEvent(order);
            }
        }

        return mapToResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId, String reason, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        checkOwnership(userId, order);

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Error: Order cannot be cancelled at this stage: " + order.getStatus());
        }

        // Handle Refund if Paid
        if (order.getStatus() == OrderStatus.PAID) {
            paymentRepository.findByOrderId(orderId).ifPresent(tx -> {
                tx.setStatus(com.phonenexus.sales.models.PaymentStatus.REFUNDED);
                paymentRepository.save(tx);
            });
            order.setStatus(OrderStatus.REFUNDED);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        order.setNote(order.getNote() + " | Cancel reason: " + reason);
        // orderRepository.save(order); -> Managed

        // Rollback stock
        for (OrderItem item : order.getItems()) {
            productClient.increaseStock(item.getVariantId(), item.getQuantity(), "INTERNAL_SECRET");
        }

        // Log history
        historyRepository.save(com.phonenexus.sales.models.OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .note("Order cancelled. Reason: " + reason)
                .build());
    }

    @Override
    public List<com.phonenexus.sales.models.OrderStatusHistory> getStatusHistory(UUID orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        checkOwnership(userId, order);
        return historyRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public com.phonenexus.sales.payload.response.SalesStatsResponse getSalesStats() {
        var allOrders = orderRepository.findAll();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.COMPLETED
                        || o.getStatus() == OrderStatus.SHIPPED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.COMPLETED
                        || o.getStatus() == OrderStatus.SHIPPED)
                .flatMap(o -> o.getItems().stream())
                .map(item -> (item.getCostPrice() != null ? item.getCostPrice() : BigDecimal.ZERO)
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalCount = allOrders.size();
        long pending = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long completed = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        long cancelled = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.REFUNDED).count();

        java.util.Map<String, Long> byMethod = allOrders.stream()
                .filter(o -> o.getPaymentMethod() != null)
                .collect(java.util.stream.Collectors.groupingBy(Order::getPaymentMethod,
                        java.util.stream.Collectors.counting()));

        java.util.Map<String, BigDecimal> monthlyProfit = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.COMPLETED
                        || o.getStatus() == OrderStatus.SHIPPED)
                .collect(java.util.stream.Collectors.groupingBy(
                        o -> {
                            java.time.LocalDateTime date = o.getCreatedAt();
                            return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                        },
                        java.util.stream.Collectors.mapping(
                                o -> {
                                    BigDecimal revenue = o.getTotalAmount();
                                    BigDecimal cost = o.getItems().stream()
                                            .map(item -> (item.getCostPrice() != null ? item.getCostPrice()
                                                    : BigDecimal.ZERO)
                                                    .multiply(new BigDecimal(item.getQuantity())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    return revenue.subtract(cost);
                                },
                                java.util.stream.Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        return com.phonenexus.sales.payload.response.SalesStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalCost(totalCost)
                .netProfit(totalRevenue.subtract(totalCost))
                .monthlyProfit(monthlyProfit)
                .totalOrders(totalCount)
                .pendingOrders(pending)
                .completedOrders(completed)
                .cancelledOrders(cancelled)
                .ordersByPaymentMethod(byMethod)
                .build();
    }

    @Override
    @Transactional
    public void processPaymentCallback(UUID orderId, boolean success, String transId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        com.phonenexus.sales.models.PaymentTransaction transaction = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for order id: " + orderId));

        if (success) {
            order.setStatus(OrderStatus.PAID);
            transaction.setStatus(com.phonenexus.sales.models.PaymentStatus.SUCCESS);
            transaction.setTransactionId(transId);

            historyRepository.save(com.phonenexus.sales.models.OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.PAID)
                    .note("Payment successful. Transaction ID: " + transId)
                    .build());

            // Mark IMEIs as SOLD
            for (OrderItem item : order.getItems()) {
                if (item.getImei() != null) {
                    try {
                        productClient.updateItemStatusByImei(item.getImei(), "SOLD", "INTERNAL-SERVICE-TOKEN-2026");
                    } catch (Exception e) {
                        log.error("Failed to mark IMEI {} as SOLD for order {}", item.getImei(), orderId, e);
                    }
                }
            }

            // Earn Loyalty Points
            if (order.getLoyaltyPoints() > 0) {
                try {
                    com.phonenexus.sales.events.LoyaltyPointsEvent pointsEvent = new com.phonenexus.sales.events.LoyaltyPointsEvent(
                            order.getUserId(),
                            order.getLoyaltyPoints(),
                            order.getTotalAmount(),
                            order.getId().toString());

                    rabbitTemplate.convertAndSend(
                            com.phonenexus.sales.config.RabbitMQConfig.EXCHANGE,
                            "loyalty.points.earned",
                            pointsEvent);
                } catch (Exception e) {
                    log.error("Failed to publish loyalty points event for order {}", orderId, e);
                }
            }

            // Publish Notification Event for Order Confirmation (PAID)
            publishOrderEvent(order);
        } else {
            transaction.setStatus(com.phonenexus.sales.models.PaymentStatus.FAILED);
            historyRepository.save(com.phonenexus.sales.models.OrderStatusHistory.builder()
                    .order(order)
                    .status(order.getStatus())
                    .note("Payment failed for transaction: " + transId)
                    .build());
        }

        // orderRepository.save(order); -> Managed
        // paymentRepository.save(transaction); -> Managed
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getVariantId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getImageUrl()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getShippingAddress(),
                order.getShippingFee(),
                order.getTaxAmount(),
                order.getDiscountAmount(),
                order.getNote(),
                order.getLoyaltyPoints(),
                order.getCreatedAt(),
                items);
    }

    private void checkOwnership(String userId, Order order) {
        // If userId is the owner, or if the "userId" passed is actually "ADMIN"
        // Note: In a real system, we'd check the SecurityContext roles.
        // For this mock hardening, we treat userId="ADMIN" as a bypass.
        if (!order.getUserId().equals(userId) && !"ADMIN".equals(userId)) {
            throw new RuntimeException("Access denied: You do not own this order and are not an Admin.");
        }
    }

    private void publishOrderEvent(Order order) {
        try {
            // Note: In a real app, 'email' and 'customerName' should come from the Identity
            // service or be stored in Order.
            // For now, we use placeholders as they aren't in the Order entity yet.
            // Ideally, we'd fetch user details here via UserClient.
            String userEmail = "customer-" + order.getUserId() + "@example.com";
            String customerName = "Customer " + order.getUserId();

            List<OrderEvent.OrderItemDetail> items = order.getItems().stream()
                    .map(item -> new OrderEvent.OrderItemDetail(
                            item.getProductName(),
                            item.getSku(),
                            item.getQuantity(),
                            item.getPrice()))
                    .collect(Collectors.toList());

            OrderEvent event = new OrderEvent(
                    order.getId(), // 1. UUID orderId
                    order.getUserId(), // 2. String userId
                    userEmail, // 3. String email
                    customerName, // 4. String customerName
                    order.getTotalAmount(), // 5. BigDecimal totalAmount
                    order.getStatus().name(), // 6. String status (e.g., PAID, SHIPPED)
                    items // 7. List<OrderEvent.OrderItemDetail>
            );

            rabbitTemplate.convertAndSend(
                    com.phonenexus.sales.config.RabbitMQConfig.EXCHANGE,
                    "order.created", // Matches order.* pattern
                    event);
            log.info("Published order event for ID: {} with status: {}", order.getId(), order.getStatus());
        } catch (Exception e) {
            log.error("Failed to publish order event for ID: {}", order.getId(), e);
        }
    }
}
