package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewCreateRequest;
import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse createReview(ReviewCreateRequest request) {
        String rawToken = UUID.randomUUID().toString();

        String hashedToken = ReviewCommandServiceImpl.hashToken(rawToken);

        Review review = Review.create(request.category(), request.rating(), request.reason(), hashedToken);
        Review saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                saved.getCategory(),
                saved.getRating(),
                saved.getReason(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}