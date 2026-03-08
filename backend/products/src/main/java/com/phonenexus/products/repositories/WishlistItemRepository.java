package com.phonenexus.products.repositories;

import com.phonenexus.products.models.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
    void deleteByWishlistIdAndProductId(UUID wishlistId, UUID productId);
}
