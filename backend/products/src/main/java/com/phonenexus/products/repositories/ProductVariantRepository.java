package com.phonenexus.products.repositories;

import com.phonenexus.products.models.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Optional<ProductVariant> findBySku(String sku);

    List<ProductVariant> findByProductIdAndIsDeletedFalse(UUID productId);

    Page<ProductVariant> findByIsDeletedFalse(Pageable pageable);
}
