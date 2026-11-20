package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewCreateRequest;
import com.schoolvoice.api.review.dto.ReviewResponse;

public interface ReviewService {
  ReviewResponse createReview(ReviewCreateRequest request);
}
