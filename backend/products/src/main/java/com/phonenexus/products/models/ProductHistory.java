package com.phonenexus.products.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_history")
public class ProductHistory {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "old_price")
    private BigDecimal oldPrice;

    @Column(name = "new_price")
    private BigDecimal newPrice;

    @Column(name = "old_stock")
    private Integer oldStock;

    @Column(name = "new_stock")
    private Integer newStock;

    @Column(name = "action_type")
    private String actionType; // e.g., "UPDATE_PRICE", "UPDATE_STOCK", "UPDATE_INFO"

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "changed_by")
    private String changedBy; // Optional: username or userId

    public ProductHistory() {
    }

    public ProductHistory(UUID productId, UUID variantId, String sku, BigDecimal oldPrice, BigDecimal newPrice,
            Integer oldStock, Integer newStock, String actionType, LocalDateTime changedAt, String changedBy) {
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

    public static ProductHistoryBuilder builder() {
        return new ProductHistoryBuilder();
    }

    // Manual Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public Integer getOldStock() {
        return oldStock;
    }

    public void setOldStock(Integer oldStock) {
        this.oldStock = oldStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public static class ProductHistoryBuilder {
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

        ProductHistoryBuilder() {
        }

        public ProductHistoryBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public ProductHistoryBuilder variantId(UUID variantId) {
            this.variantId = variantId;
            return this;
        }

        public ProductHistoryBuilder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public ProductHistoryBuilder oldPrice(BigDecimal oldPrice) {
            this.oldPrice = oldPrice;
            return this;
        }

        public ProductHistoryBuilder newPrice(BigDecimal newPrice) {
            this.newPrice = newPrice;
            return this;
        }

        public ProductHistoryBuilder oldStock(Integer oldStock) {
            this.oldStock = oldStock;
            return this;
        }

        public ProductHistoryBuilder newStock(Integer newStock) {
            this.newStock = newStock;
            return this;
        }

        public ProductHistoryBuilder actionType(String actionType) {
            this.actionType = actionType;
            return this;
        }

        public ProductHistoryBuilder changedAt(LocalDateTime changedAt) {
            this.changedAt = changedAt;
            return this;
        }

        public ProductHistoryBuilder changedBy(String changedBy) {
            this.changedBy = changedBy;
            return this;
        }

        public ProductHistory build() {
            return new ProductHistory(productId, variantId, sku, oldPrice, newPrice, oldStock, newStock, actionType,
                    changedAt, changedBy);
        }
    }
}
