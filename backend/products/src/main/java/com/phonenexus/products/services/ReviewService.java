package com.phonenexus.products.services;

import com.phonenexus.products.payload.request.ReviewRequest;
import com.phonenexus.products.payload.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(UUID userId, ReviewRequest request);

    Page<ReviewResponse> getReviewsByProduct(UUID productId, Pageable pageable);

    void deleteReview(UUID userId, UUID reviewId);

    double getAverageRating(UUID productId);
}
