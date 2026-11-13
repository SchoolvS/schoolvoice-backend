package com.schoolvoice.api.review.dto;

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
}
