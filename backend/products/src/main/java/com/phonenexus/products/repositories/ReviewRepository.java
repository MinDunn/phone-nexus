package com.phonenexus.products.repositories;

import com.phonenexus.products.models.Product;
import com.phonenexus.products.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProduct(Product product);

    List<Review> findByProductId(UUID productId);

    org.springframework.data.domain.Page<Review> findByProductId(UUID productId,
            org.springframework.data.domain.Pageable pageable);

    List<Review> findByUserId(UUID userId);
}
