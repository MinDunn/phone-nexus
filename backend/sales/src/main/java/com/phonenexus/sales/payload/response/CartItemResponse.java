package com.phonenexus.sales.payload.response;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
    private BigDecimal subTotal;

    public CartItemResponse(UUID id, UUID productId, String productName, Integer quantity, BigDecimal price,
            String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.subTotal = price.multiply(new BigDecimal(quantity));
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }
}
