package com.schoolvoice.api.stats.service;

import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.stats.dto.CategoryStatisticsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChartServiceTest {

    @InjectMocks
    private ChartServiceImpl chartService;

    @Mock
    private StatisticsService statisticsService;

    @Test
    @DisplayName("정상 생성: 카테고리별 통계가 있으면 600x400 PNG 이미지가 만들어진다.")
    void createCategoryAverageChart_Success() throws IOException {
        //given
        given(statisticsService.getCategoryStatistics()).willReturn(List.of(
                new CategoryStatisticsResponse(ReviewCategory.FOOD, 3, new BigDecimal("4.33")),
                new CategoryStatisticsResponse(ReviewCategory.DORMITORY, 2, new BigDecimal("2.50"))
        ));

        //when
        byte[] chart = chartService.createCategoryAverageChart();

        //then
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(chart));
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(600);
        assertThat(image.getHeight()).isEqualTo(400);
    }

    @Test
    @DisplayName("빈 데이터: 리뷰가 하나도 없어도 예외 없이 PNG 이미지가 만들어진다.")
    void createCategoryAverageChart_EmptyData() throws IOException {
        //given
        given(statisticsService.getCategoryStatistics()).willReturn(List.of(
                new CategoryStatisticsResponse(ReviewCategory.FOOD, 0, null),
                new CategoryStatisticsResponse(ReviewCategory.SCHOOL, 0, null)
        ));

        //when
        byte[] chart = chartService.createCategoryAverageChart();

        //then
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(chart));
        assertThat(image).isNotNull();
    }
}
