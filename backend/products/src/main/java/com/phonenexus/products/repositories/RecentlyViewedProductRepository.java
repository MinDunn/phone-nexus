package com.phonenexus.products.repositories;

import com.phonenexus.products.models.RecentlyViewedProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecentlyViewedProductRepository extends JpaRepository<RecentlyViewedProduct, UUID> {
    List<RecentlyViewedProduct> findByUserIdOrderByViewedAtDesc(String userId, Pageable pageable);

    Optional<RecentlyViewedProduct> findByUserIdAndProductId(String userId, UUID productId);
}
