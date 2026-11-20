package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewPageResponse;
import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.dto.ReviewSort;
import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.exception.ReviewNotFoundException;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(ReviewReadServiceImpl.class)
@Sql(statements = "delete from reviews")
class ReviewReadServiceTest {

    private static final String TEST_TOKEN_HASH = "test-hash";

    @Autowired
    private ReviewReadService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void returnsSavedReviewDetails() {
        Review saved = reviewRepository.save(Review.create(ReviewCategory.FOOD, 4, "급식이 맛있어요", TEST_TOKEN_HASH));

        ReviewResponse review = reviewService.getReview(saved.getId());

        assertThat(review.id()).isEqualTo(saved.getId());
        assertThat(review.category()).isEqualTo(ReviewCategory.FOOD);
        assertThat(review.rating()).isEqualTo(4);
        assertThat(review.reason()).isEqualTo("급식이 맛있어요");
    }

    @Test
    void throwsWhenReviewDoesNotExist() {
        assertThatThrownBy(() -> reviewService.getReview(1L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void filtersByCategoryAndPagesInDescendingRatingOrder() {
        reviewRepository.save(Review.create(ReviewCategory.FOOD, 2, "메뉴가 아쉬워요", TEST_TOKEN_HASH));
        reviewRepository.save(Review.create(ReviewCategory.FOOD, 5, "반찬이 좋아요", TEST_TOKEN_HASH));
        reviewRepository.save(Review.create(ReviewCategory.CLASS, 4, "설명이 쉬워요", TEST_TOKEN_HASH));

        ReviewPageResponse first = reviewService.getReviews(ReviewCategory.FOOD, ReviewSort.RATING_DESC, 0, 1);
        ReviewPageResponse second = reviewService.getReviews(ReviewCategory.FOOD, ReviewSort.RATING_DESC, 1, 1);

        assertThat(first.totalElements()).isEqualTo(2);
        assertThat(first.totalPages()).isEqualTo(2);
        assertThat(first.content()).extracting(ReviewResponse::rating).containsExactly(5);
        assertThat(second.content()).extracting(ReviewResponse::rating).containsExactly(2);
    }
}
