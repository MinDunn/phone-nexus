package com.phonenexus.sales.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderEvent(
                UUID orderId,
                String userId,
                String email,
                String customerName,
                BigDecimal totalAmount,
                String status,
                List<OrderItemDetail> items) {
        public record OrderItemDetail(
                        String productName,
                        String sku,
                        Integer quantity,
                        BigDecimal price) {
        }
}
