package com.phonenexus.products.services;

import com.phonenexus.products.payload.response.ProductResponse;
import java.util.List;
import java.util.UUID;

public interface WishlistService {
    void addToWishlist(UUID userId, UUID productId);

    void removeFromWishlist(UUID userId, UUID productId);

    List<ProductResponse> getWishlist(UUID userId);

    boolean isInWishlist(UUID userId, UUID productId);
}
