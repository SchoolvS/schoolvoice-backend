package com.schoolvoice.api.stats.controller;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.repository.ReviewRepository;
import com.schoolvoice.api.stats.service.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@Import({StatisticsServiceImpl.class, StatisticsController.class})
@Sql(statements = "delete from reviews")
class StatisticsControllerTest {

    private static final String TEST_TOKEN_HASH = "test-hash";

    private static final String STATISTICS = "/api/v1/statistics";

    private MockMvc mockMvc;

    @Autowired
    private StatisticsController statisticsController;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    void calculatesCategoryAndOverallAveragesFromIndividualReviews() throws Exception {
        for (int i = 0; i < 7; i++) {
            reviewRepository.save(Review.create(ReviewCategory.FOOD, 3, "급식 평가", TEST_TOKEN_HASH));
        }
        reviewRepository.save(Review.create(ReviewCategory.FOOD, 4, "급식 평가", TEST_TOKEN_HASH));
        reviewRepository.save(Review.create(ReviewCategory.CLASS, 2, "수업 평가", TEST_TOKEN_HASH));

        mockMvc.perform(get(STATISTICS + "/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.category == 'FOOD')].reviewCount", contains(8)))
                .andExpect(jsonPath("$[?(@.category == 'FOOD')].averageRating", contains(3.13)))
                .andExpect(jsonPath("$[?(@.category == 'CLASS')].reviewCount", contains(1)))
                .andExpect(jsonPath("$[?(@.category == 'CLASS')].averageRating", contains(2.0)));

        mockMvc.perform(get(STATISTICS + "/overall"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount").value(9))
                .andExpect(jsonPath("$.averageRating").value(3.0));
    }

    @Test
    void returnsAllFiveRatingCountsForOnlyTheRequestedCategory() throws Exception {
        reviewRepository.save(Review.create(ReviewCategory.FOOD, 4, "반찬 평가", TEST_TOKEN_HASH));
        reviewRepository.save(Review.create(ReviewCategory.FOOD, 4, "메뉴 평가", TEST_TOKEN_HASH));
        reviewRepository.save(Review.create(ReviewCategory.CLASS, 1, "수업 평가", TEST_TOKEN_HASH));

        mockMvc.perform(get(STATISTICS + "/distribution").param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].rating", contains(1, 2, 3, 4, 5)))
                .andExpect(jsonPath("$[*].count", contains(0, 0, 0, 2, 0)));
    }

    @Test
    void returnsNullAveragesAndZeroCountsWhenThereAreNoReviews() throws Exception {
        mockMvc.perform(get(STATISTICS + "/overall"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount").value(0))
                .andExpect(jsonPath("$.averageRating").hasJsonPath())
                .andExpect(jsonPath("$.averageRating").value(nullValue()));

        mockMvc.perform(get(STATISTICS + "/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(ReviewCategory.values().length)))
                .andExpect(jsonPath("$[*].reviewCount", everyItem(is(0))))
                .andExpect(jsonPath("$[*].averageRating", hasSize(ReviewCategory.values().length)))
                .andExpect(jsonPath("$[*].averageRating", everyItem(nullValue())));

        mockMvc.perform(get(STATISTICS + "/distribution").param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].count", contains(0, 0, 0, 0, 0)));
    }

    @Test
    void rejectsMissingOrUnknownDistributionCategory() throws Exception {
        mockMvc.perform(get(STATISTICS + "/distribution"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(STATISTICS + "/distribution").param("category", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }
}
