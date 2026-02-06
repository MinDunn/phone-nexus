package com.phonenexus.sales.payload.response;

import com.phonenexus.sales.models.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    private UUID id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String paymentMethod;
    private String shippingAddress;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String note;
    private java.time.LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(UUID id, String userId, BigDecimal totalAmount, OrderStatus status, String paymentMethod,
            String shippingAddress, BigDecimal shippingFee, BigDecimal taxAmount, BigDecimal discountAmount,
            String note, LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.shippingFee = shippingFee;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.note = note;
        this.createdAt = createdAt;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
