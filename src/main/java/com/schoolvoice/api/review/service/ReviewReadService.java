package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewPageResponse;
import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.dto.ReviewSort;
import com.schoolvoice.api.review.entity.ReviewCategory;

public interface ReviewReadService {

    ReviewPageResponse getReviews(ReviewCategory category, ReviewSort sort, int page, int size);

    ReviewResponse getReview(Long id);
}
