package com.phonenexus.products.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    public Category() {
    }

    public Category(String name, String description, Category parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
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

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    // Builder Pattern
    public static class CategoryBuilder {
        private String name;
        private String description;
        private Category parent;

        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CategoryBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CategoryBuilder parent(Category parent) {
            this.parent = parent;
            return this;
        }

        public Category build() {
            return new Category(name, description, parent);
        }
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }
}
