package com.phonenexus.sales.services;

import com.phonenexus.sales.payload.request.OrderRequest;
import com.phonenexus.sales.payload.response.OrderResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(String userId, OrderRequest request);

    OrderResponse getOrderDetails(UUID orderId, String userId);

    Page<OrderResponse> getOrderHistory(String userId, Pageable pageable);

    OrderResponse updateOrderStatus(UUID orderId, String status, String userId);

    void cancelOrder(UUID orderId, String reason, String userId);

    List<com.phonenexus.sales.models.OrderStatusHistory> getStatusHistory(UUID orderId, String userId);

    void processPaymentCallback(UUID orderId, boolean success, String transId);

    // Admin APIs
    Page<OrderResponse> getAllOrders(Pageable pageable);

    com.phonenexus.sales.payload.response.SalesStatsResponse getSalesStats();
}
