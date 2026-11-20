package com.schoolvoice.api.review.controller;

import com.schoolvoice.api.review.dto.ReviewCreateRequest;
import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewCreateController.class)
class ReviewCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    @DisplayName("정상 등록: 올바른 요청이면 201과 등록된 리뷰를 응답한다.")
    void createReview_Success() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2026, 11, 20, 12, 0);
        ReviewResponse response = new ReviewResponse(1L, ReviewCategory.FOOD, 4, "급식이 맛있어서 만족합니다", now, now);
        given(reviewService.createReview(any(ReviewCreateRequest.class))).willReturn(response);

        String body = """
                {"category": "FOOD", "rating": 4, "reason": "급식이 맛있어서 만족합니다"}
                """;

        //when //then
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.reason").value("급식이 맛있어서 만족합니다"));
    }

    @Test
    @DisplayName("실패 사례 (400): 평점이 범위를 벗어나면 등록되지 않는다.")
    void createReview_Fail_InvalidRating() throws Exception {
        //given
        String body = """
                {"category": "FOOD", "rating": 6, "reason": "급식이 맛있어서 만족합니다"}
                """;

        //when //then
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any());
    }

    @Test
    @DisplayName("실패 사례 (400): 이유가 10자 미만이면 등록되지 않는다.")
    void createReview_Fail_ShortReason() throws Exception {
        //given
        String body = """
                {"category": "FOOD", "rating": 4, "reason": "짧음"}
                """;

        //when //then
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any());
    }

    @Test
    @DisplayName("실패 사례 (400): 카테고리가 없으면 등록되지 않는다.")
    void createReview_Fail_MissingCategory() throws Exception {
        //given
        String body = """
                {"rating": 4, "reason": "급식이 맛있어서 만족합니다"}
                """;

        //when //then
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any());
    }
}
