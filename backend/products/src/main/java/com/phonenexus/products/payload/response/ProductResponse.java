package com.phonenexus.products.payload.response;

import java.util.List;
import java.util.UUID;

public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID brandId;
    private String brandName;
    private UUID categoryId;
    private String categoryName;
    private String status;
    private List<ProductVariantResponse> variants;

    public ProductResponse(UUID id, String name, String description, UUID brandId, String brandName, UUID categoryId,
            String categoryName, String status, List<ProductVariantResponse> variants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
        this.variants = variants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ProductVariantResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantResponse> variants) {
        this.variants = variants;
    }
}
