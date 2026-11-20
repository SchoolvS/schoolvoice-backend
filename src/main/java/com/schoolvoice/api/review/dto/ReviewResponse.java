package com.schoolvoice.api.review.dto;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        ReviewCategory category,
        int rating,
        String reason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getCategory(),
                review.getRating(),
                review.getReason(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}