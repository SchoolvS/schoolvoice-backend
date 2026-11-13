package com.schoolvoice.api.review.dto;

import java.util.List;

public record ReviewPageResponse(
        List<ReviewResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
