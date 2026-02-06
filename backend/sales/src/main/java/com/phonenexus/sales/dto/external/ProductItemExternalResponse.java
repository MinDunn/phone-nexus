package com.phonenexus.sales.dto.external;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductItemExternalResponse {
    private UUID id;
    private String imei;
    private BigDecimal costPrice;

    public ProductItemExternalResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
}
