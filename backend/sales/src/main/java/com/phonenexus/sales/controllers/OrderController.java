package com.phonenexus.sales.controllers;

import com.phonenexus.sales.payload.request.OrderRequest;
import com.phonenexus.sales.payload.response.OrderResponse;
import com.phonenexus.sales.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.phonenexus.sales.payload.response.SalesStatsResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.checkout(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(orderService.getOrderDetails(id, userId));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrderHistory(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getOrderHistory(userId, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Role", required = false) String role) {

        String effectiveUserId = "ADMIN".equals(role) ? "ADMIN" : userId;
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, effectiveUserId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Role", required = false) String role) {

        String effectiveUserId = "ADMIN".equals(role) ? "ADMIN" : userId;
        orderService.cancelOrder(id, reason, effectiveUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<com.phonenexus.sales.models.OrderStatusHistory>> getStatusHistory(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Role", required = false) String role) {

        String effectiveUserId = "ADMIN".equals(role) ? "ADMIN" : userId;
        return ResponseEntity.ok(orderService.getStatusHistory(id, effectiveUserId));
    }

    // --- Admin Endpoints ---

    @GetMapping("/admin/all")
    public ResponseEntity<Page<OrderResponse>> getAllOrdersAdmin(
            @RequestHeader("X-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<SalesStatsResponse> getSalesStats(
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(orderService.getSalesStats());
    }
}
