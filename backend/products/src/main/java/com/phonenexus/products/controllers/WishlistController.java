package com.phonenexus.products.controllers;

import com.phonenexus.products.payload.response.ApiResponse;
import com.phonenexus.products.payload.response.ProductResponse;
import com.phonenexus.products.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
            @PathVariable UUID productId,
            @RequestParam UUID userId) { // For simplicity, using userId param. In real app, get from principal
        wishlistService.addToWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", null));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable UUID productId,
            @RequestParam UUID userId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlist(@RequestParam UUID userId) {
        return ResponseEntity
                .ok(ApiResponse.success("Wishlist fetched successfully", wishlistService.getWishlist(userId)));
    }
}
