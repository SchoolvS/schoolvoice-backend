package com.schoolvoice.api.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.service.ReviewCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewCommandController.class)
class ReviewCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewCommandService reviewCommandService;

    @Test
    @DisplayName("실패 사례 (400): 평점이 범위를 벗어나면 유효성 검사에 실패한다.")
    void updateReview_Fail_BadRequest_InvalidRating() throws Exception {
        // 객체 생성 및 ObjectMapper 변환 대신 JSON 문자열을 직접 작성
        String invalidJsonContent = "{\"rating\": 6, \"reason\": \"너무 좋아요\"}";

        mockMvc.perform(put("/api/reviews/1")
                        .header("X-Edit-Token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonContent))
                .andExpect(status().isBadRequest());
    }
}