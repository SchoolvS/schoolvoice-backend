package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewPageResponse;
import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.dto.ReviewSort;
import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.exception.ReviewNotFoundException;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewReadServiceImpl implements ReviewReadService {

    private final ReviewRepository reviewRepository;

    public ReviewReadServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewPageResponse getReviews(ReviewCategory category, ReviewSort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, toSort(sort));

        Page<Review> reviews;
        if (category == null) {
            reviews = reviewRepository.findAll(pageable);
        } else {
            reviews = reviewRepository.findByCategory(category, pageable);
        }

        List<ReviewResponse> content = reviews.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new ReviewPageResponse(
                content,
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements(),
                reviews.getTotalPages()
        );
    }

    @Override
    public ReviewResponse getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        return toResponse(review);
    }

    private Sort toSort(ReviewSort sort) {
        switch (sort) {
            case RATING_DESC:
                return Sort.by(Sort.Direction.DESC, "rating");
            case RATING_ASC:
                return Sort.by(Sort.Direction.ASC, "rating");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }

    private ReviewResponse toResponse(Review review) {
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
