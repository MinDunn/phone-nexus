package com.phonenexus.sales.payload.response;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;

    public OrderItemResponse(UUID id, UUID productId, UUID variantId, String productName, String sku, BigDecimal price,
            Integer quantity, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.sku = sku;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSku() {
        return sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
