package com.phonenexus.products.payload.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewResponse {
    private UUID id;
    private UUID productId;
    private UUID userId;
    private String username; // Optional, if we can fetch it
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
