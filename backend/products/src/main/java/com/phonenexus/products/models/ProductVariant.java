package com.phonenexus.products.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true)
    private String sku;

    private String color;

    private String storageCapacity;

    private String ram;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    private String imageUrl;

    public ProductVariant() {
    }

    public ProductVariant(Product product, String sku, String color, String storageCapacity, String ram,
            BigDecimal price, Integer stockQuantity, String imageUrl) {
        this.product = product;
        this.sku = sku;
        this.color = color;
        this.storageCapacity = storageCapacity;
        this.ram = ram;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Builder Pattern
    public static class ProductVariantBuilder {
        private Product product;
        private String sku;
        private String color;
        private String storageCapacity;
        private String ram;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;

        public ProductVariantBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public ProductVariantBuilder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public ProductVariantBuilder color(String color) {
            this.color = color;
            return this;
        }

        public ProductVariantBuilder storageCapacity(String storageCapacity) {
            this.storageCapacity = storageCapacity;
            return this;
        }

        public ProductVariantBuilder ram(String ram) {
            this.ram = ram;
            return this;
        }

        public ProductVariantBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductVariantBuilder stockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public ProductVariantBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ProductVariant build() {
            return new ProductVariant(product, sku, color, storageCapacity, ram, price, stockQuantity, imageUrl);
        }
    }

    public static ProductVariantBuilder builder() {
        return new ProductVariantBuilder();
    }
}
