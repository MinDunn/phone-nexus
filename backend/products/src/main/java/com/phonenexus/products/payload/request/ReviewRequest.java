package com.phonenexus.products.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class ReviewRequest {
    private UUID productId;

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    private String comment;
}
