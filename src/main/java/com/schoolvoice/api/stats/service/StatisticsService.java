package com.schoolvoice.api.stats.service;

import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.stats.dto.CategoryStatisticsResponse;
import com.schoolvoice.api.stats.dto.OverallStatisticsResponse;
import com.schoolvoice.api.stats.dto.RatingCount;

import java.util.List;

public interface StatisticsService {

    List<CategoryStatisticsResponse> getCategoryStatistics();

    OverallStatisticsResponse getOverallStatistics();

    List<RatingCount> getDistribution(ReviewCategory category);
}
