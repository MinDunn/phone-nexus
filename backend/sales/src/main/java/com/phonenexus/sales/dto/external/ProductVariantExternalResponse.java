package com.phonenexus.sales.dto.external;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductVariantExternalResponse {
    private UUID id;
    private String sku;
    private BigDecimal price;
    private String imageUrl;

    public ProductVariantExternalResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
