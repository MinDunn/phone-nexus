package com.phonenexus.products.services.impl;

import com.phonenexus.products.models.Product;
import com.phonenexus.products.models.Review;
import com.phonenexus.products.payload.request.ReviewRequest;
import com.phonenexus.products.payload.response.ReviewResponse;
import com.phonenexus.products.repositories.ProductRepository;
import com.phonenexus.products.repositories.ReviewRepository;
import com.phonenexus.products.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private com.phonenexus.products.clients.UserClient userClient;

    @Override
    @Transactional
    public ReviewResponse createReview(UUID userId, ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = new Review();
        review.setProduct(product);
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsByProduct(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteReview(UUID userId, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Override
    public double getAverageRating(UUID productId) {
        return reviewRepository.findByProductId(productId).stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private ReviewResponse mapToResponse(Review review) {
        String username = "Unknown";
        try {
            com.phonenexus.products.dto.external.UserExternalResponse user = userClient.getUserById(review.getUserId());
            if (user != null) {
                username = user.getUsername();
            }
        } catch (Exception e) {
            // Log error but continue
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUserId())
                .username(username)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
