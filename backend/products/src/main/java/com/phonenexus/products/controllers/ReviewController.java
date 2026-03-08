package com.phonenexus.products.controllers;

import com.phonenexus.products.payload.request.ReviewRequest;
import com.phonenexus.products.payload.response.ApiResponse;
import com.phonenexus.products.payload.response.ReviewResponse;
import com.phonenexus.products.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @RequestParam UUID userId) {
        return ResponseEntity
                .ok(ApiResponse.success("Review submitted successfully", reviewService.createReview(userId, request)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Product reviews fetched successfully",
                reviewService.getReviewsByProduct(productId, pageable)));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable UUID reviewId,
            @RequestParam UUID userId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
