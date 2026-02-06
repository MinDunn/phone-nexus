package com.phonenexus.products.repositories;

import com.phonenexus.products.models.ItemStatus;
import com.phonenexus.products.models.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
    Optional<ProductItem> findByImei(String imei);

    List<ProductItem> findByVariantIdAndStatus(UUID variantId, ItemStatus status);

    @Query(value = "SELECT * FROM product_items WHERE variant_id = :variantId AND status = 'AVAILABLE' LIMIT :limit", nativeQuery = true)
    List<ProductItem> findAvailableItems(@Param("variantId") UUID variantId, @Param("limit") int limit);

    long countByVariantIdAndStatus(UUID variantId, ItemStatus status);
}
