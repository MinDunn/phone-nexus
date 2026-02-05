package com.phonenexus.products.repositories;

import com.phonenexus.products.models.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, UUID> {
    List<ProductHistory> findByProductIdOrderByChangedAtDesc(UUID productId);

    List<ProductHistory> findByVariantIdOrderByChangedAtDesc(UUID variantId);
}
