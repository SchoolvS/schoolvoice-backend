package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.dto.ReviewUpdateResponse;

public interface ReviewCommandService {
    ReviewUpdateResponse updateReview(Long id, String editToken, ReviewUpdateRequest request);
    void deleteReview(Long id, String editToken);
}