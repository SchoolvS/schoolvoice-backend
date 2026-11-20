package com.schoolvoice.api.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.service.ReviewCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewCommandController.class)
class ReviewCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewCommandService reviewCommandService;

    @Test
    @DisplayName("실패 사례 (400): 평점이 범위를 벗어나면 유효성 검사에 실패한다.")
    void updateReview_Fail_BadRequest_InvalidRating() throws Exception {
        ReviewUpdateRequest invalidRequest = new ReviewUpdateRequest(6, "너무 좋아요");

        mockMvc.perform(put("/api/reviews/1")
                        .header("X-Edit-Token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}