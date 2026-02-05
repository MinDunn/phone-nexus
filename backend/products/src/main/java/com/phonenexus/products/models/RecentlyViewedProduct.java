package com.phonenexus.products.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recently_viewed_products")
public class RecentlyViewedProduct {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId; // String because JWT sub is usually string, though could be UUID

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public RecentlyViewedProduct() {
    }

    public RecentlyViewedProduct(String userId, UUID productId, LocalDateTime viewedAt) {
        this.userId = userId;
        this.productId = productId;
        this.viewedAt = viewedAt;
    }

    // Manual Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    public static RecentlyViewedBuilder builder() {
        return new RecentlyViewedBuilder();
    }

    public static class RecentlyViewedBuilder {
        private String userId;
        private UUID productId;
        private LocalDateTime viewedAt;

        public RecentlyViewedBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public RecentlyViewedBuilder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public RecentlyViewedBuilder viewedAt(LocalDateTime viewedAt) {
            this.viewedAt = viewedAt;
            return this;
        }

        public RecentlyViewedProduct build() {
            return new RecentlyViewedProduct(userId, productId, viewedAt);
        }
    }
}
