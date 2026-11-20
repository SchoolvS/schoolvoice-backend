package com.schoolvoice.api.review.dto;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;

import java.time.LocalDateTime;

public record ReviewUpdateResponse(
        Long id,
        ReviewCategory category,
        int rating,
        String reason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewUpdateResponse from(Review review) {
        return new ReviewUpdateResponse(
                review.getId(),
                review.getCategory(),
                review.getRating(),
                review.getReason(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}