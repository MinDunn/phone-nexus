package com.phonenexus.sales.dto.external;

import java.util.List;
import java.util.UUID;

public class ProductExternalResponse {
    private UUID id;
    private String name;
    private List<ProductVariantExternalResponse> variants;

    public ProductExternalResponse() {
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

    public List<ProductVariantExternalResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantExternalResponse> variants) {
        this.variants = variants;
    }
}
