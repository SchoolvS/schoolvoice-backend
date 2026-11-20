package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.dto.ReviewUpdateRequest;

public interface ReviewUpdateService {

    ReviewResponse updateReview(Long id, String editToken, ReviewUpdateRequest request);

    void deleteReview(Long id, String editToken);
}