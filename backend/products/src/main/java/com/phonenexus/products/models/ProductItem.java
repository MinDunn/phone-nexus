package com.phonenexus.products.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_items")
public class ProductItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(unique = true, length = 15)
    private String imei;

    @Column(name = "cost_price", nullable = false)
    private BigDecimal costPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    public ProductItem() {
    }

    private ProductItem(Builder builder) {
        this.variant = builder.variant;
        this.imei = builder.imei;
        this.costPrice = builder.costPrice;
        this.status = builder.status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
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

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt = soldAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProductVariant variant;
        private String imei;
        private BigDecimal costPrice;
        private ItemStatus status = ItemStatus.AVAILABLE;

        public Builder variant(ProductVariant variant) {
            this.variant = variant;
            return this;
        }

        public Builder imei(String imei) {
            this.imei = imei;
            return this;
        }

        public Builder costPrice(BigDecimal costPrice) {
            this.costPrice = costPrice;
            return this;
        }

        public Builder status(ItemStatus status) {
            this.status = status;
            return this;
        }

        public ProductItem build() {
            return new ProductItem(this);
        }
    }
}
