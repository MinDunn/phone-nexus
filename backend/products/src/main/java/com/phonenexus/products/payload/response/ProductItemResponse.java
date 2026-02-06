package com.phonenexus.products.payload.response;

import com.phonenexus.products.models.ItemStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductItemResponse {
    private UUID id;
    private String imei;
    private BigDecimal costPrice;
    private ItemStatus status;
    private LocalDateTime importedAt;
    private LocalDateTime soldAt;

    public ProductItemResponse() {
    }

    public ProductItemResponse(UUID id, String imei, BigDecimal costPrice, ItemStatus status, LocalDateTime importedAt,
            LocalDateTime soldAt) {
        this.id = id;
        this.imei = imei;
        this.costPrice = costPrice;
        this.status = status;
        this.importedAt = importedAt;
        this.soldAt = soldAt;
    }

    // Getters and Setters
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

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt = soldAt;
    }
}
