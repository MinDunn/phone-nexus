package com.phonenexus.products.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "brands")
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    private String logoUrl;

    public Brand() {
    }

    public Brand(String name, String description, String logoUrl) {
        this.name = name;
        this.description = description;
        this.logoUrl = logoUrl;
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    // Builder Pattern
    public static class BrandBuilder {
        private String name;
        private String description;
        private String logoUrl;

        public BrandBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BrandBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BrandBuilder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public Brand build() {
            return new Brand(name, description, logoUrl);
        }
    }

    public static BrandBuilder builder() {
        return new BrandBuilder();
    }
}
