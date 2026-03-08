package com.phonenexus.products.services.impl;

import com.phonenexus.products.models.Product;
import com.phonenexus.products.models.Wishlist;
import com.phonenexus.products.models.WishlistItem;
import com.phonenexus.products.payload.response.ProductResponse;
import com.phonenexus.products.repositories.ProductRepository;
import com.phonenexus.products.repositories.WishlistItemRepository;
import com.phonenexus.products.repositories.WishlistRepository;
import com.phonenexus.products.services.ProductService;
import com.phonenexus.products.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void addToWishlist(UUID userId, UUID productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUserId(userId);
                    return wishlistRepository.save(newWishlist);
                });

        boolean exists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));

        if (!exists) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            WishlistItem item = new WishlistItem();
            item.setWishlist(wishlist);
            item.setProduct(product);
            wishlist.getItems().add(item);
            wishlistRepository.save(wishlist);
        }
    }

    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        wishlistRepository.findByUserId(userId).ifPresent(wishlist -> {
            wishlist.getItems()
                    .removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
            wishlistRepository.save(wishlist);
        });
    }

    @Override
    public List<ProductResponse> getWishlist(UUID userId) {
        return wishlistRepository.findByUserId(userId)
                .map(wishlist -> wishlist.getItems().stream()
                        .map(item -> productService.getProductById(item.getProduct().getId()))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public boolean isInWishlist(UUID userId, UUID productId) {
        return wishlistRepository.findByUserId(userId)
                .map(wishlist -> wishlist.getItems().stream()
                        .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId)))
                .orElse(false);
    }
}
