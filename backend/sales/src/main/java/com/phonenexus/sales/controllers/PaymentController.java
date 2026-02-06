package com.phonenexus.sales.controllers;

import com.phonenexus.sales.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/callback/{orderId}")
    public ResponseEntity<String> paymentCallback(
            @PathVariable UUID orderId,
            @RequestParam boolean success,
            @RequestParam String transactionId,
            @RequestHeader(value = "X-Payment-Token", required = false) String token) {

        // SECURITY: Verify token (In real world, this is a webhook signature)
        // For mock, we use a simple secret
        if (!"PAYMENT-SECRET-CONFIRMED-2026".equals(token)) {
            return ResponseEntity.status(403).body("Access Denied: Invalid payment token.");
        }

        orderService.processPaymentCallback(orderId, success, transactionId);

        if (success) {
            return ResponseEntity.ok("Payment processed successfully for order: " + orderId);
        } else {
            return ResponseEntity.badRequest().body("Payment failed for order: " + orderId);
        }
    }
}
