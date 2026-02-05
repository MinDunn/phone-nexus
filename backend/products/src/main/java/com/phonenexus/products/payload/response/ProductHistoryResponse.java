package com.phonenexus.products.payload.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductHistoryResponse {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String sku;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private Integer oldStock;
    private Integer newStock;
    private String actionType;
    private LocalDateTime changedAt;
    private String changedBy;

    public ProductHistoryResponse(UUID id, UUID productId, UUID variantId, String sku, BigDecimal oldPrice,
            BigDecimal newPrice, Integer oldStock, Integer newStock, String actionType, LocalDateTime changedAt,
            String changedBy) {
        this.id = id;
        this.productId = productId;
        this.variantId = variantId;
        this.sku = sku;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.actionType = actionType;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
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

    public String getSku() {
        return sku;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public Integer getOldStock() {
        return oldStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public String getActionType() {
        return actionType;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }
}
