package com.phonenexus.notifications.events;

import java.util.UUID;

public record StockEvent(
        UUID variantId,
        String sku,
        String productName,
        Integer currentStock) {
}
