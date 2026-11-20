package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.dto.ReviewUpdateResponse;
import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewCommandServiceTest {

    @InjectMocks
    private ReviewCommandServiceImpl reviewCommandService;

    @Mock
    private ReviewRepository reviewRepository;

    private final String rawToken = "my-secret-token";
    private String hashedToken;
    private Review review;

    @BeforeEach
    void setUp() {
        hashedToken = ReviewCommandServiceImpl.hashToken(rawToken);
        review = Review.create(ReviewCategory.TEACHING, 3, "원래 이유", hashedToken);
    }

    @Test
    @DisplayName("정상 수정: 올바른 토큰과 입력값으로 DB 엔티티가 변경")
    void updateReview_Success() {
        Long reviewId = 1L;
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "수정된 이유");

        ReviewUpdateResponse response = reviewCommandService.updateReview(reviewId, rawToken, request);

        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.reason()).isEqualTo("수정된 이유");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getReason()).isEqualTo("수정된 이유");
    }

    @Test
    @DisplayName("정상 삭제: 올바른 토큰으로 repository - delete가 호출")
    void deleteReview_Success() {
        Long reviewId = 1L;
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        reviewCommandService.deleteReview(reviewId, rawToken);

        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("실패 사례 (404): 없는 ID 수정 시도 시 차단")
    void updateReview_Fail_NotFound() {
        Long invalidId = 999L;
        given(reviewRepository.findById(invalidId)).willReturn(Optional.empty());
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "이유");

        assertThatThrownBy(() -> reviewCommandService.updateReview(invalidId, rawToken, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    @DisplayName("실패 사례 (403): 잘못된 토큰으로 수정 시도 시 차단되고 엔티티는 변경되지 않음")
    void updateReview_Fail_InvalidToken() {
        Long reviewId = 1L;
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, "수정 시도");

        assertThatThrownBy(() -> reviewCommandService.updateReview(reviewId, "wrong-token", request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(HttpStatus.FORBIDDEN.toString());

        assertThat(review.getRating()).isEqualTo(3);
    }
}