package com.phonenexus.products.repositories.specs;

import com.phonenexus.products.models.Product;
import com.phonenexus.products.models.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> filterProducts(
            UUID brandId,
            UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String name) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }

            if (minPrice != null || maxPrice != null) {
                Join<Product, ProductVariant> variants = root.join("variants");
                predicates.add(criteriaBuilder.equal(variants.get("isDeleted"), false));

                if (minPrice != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(variants.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(variants.get("price"), maxPrice));
                }
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
