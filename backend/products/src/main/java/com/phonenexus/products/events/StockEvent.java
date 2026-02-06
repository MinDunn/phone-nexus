package com.phonenexus.products.events;

import java.util.UUID;

public record StockEvent(
        UUID variantId,
        String sku,
        String productName,
        Integer currentStock) {
}
