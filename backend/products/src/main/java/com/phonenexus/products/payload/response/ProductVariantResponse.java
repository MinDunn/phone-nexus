package com.phonenexus.products.payload.response;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductVariantResponse {
    private UUID id;
    private String sku;
    private String color;
    private String storageCapacity;
    private String ram;
    private BigDecimal price;
    private String imageUrl;

    public ProductVariantResponse(UUID id, String sku, String color, String storageCapacity, String ram,
            BigDecimal price, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.color = color;
        this.storageCapacity = storageCapacity;
        this.ram = ram;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(String storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
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
