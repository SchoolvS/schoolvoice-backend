package com.schoolvoice.api.stats.service;

import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.repository.ReviewRepository;
import com.schoolvoice.api.review.repository.ReviewRepository.CategoryTotals;
import com.schoolvoice.api.stats.dto.CategoryStatisticsResponse;
import com.schoolvoice.api.stats.dto.OverallStatisticsResponse;
import com.schoolvoice.api.stats.dto.RatingCount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ReviewRepository reviewRepository;

    public StatisticsServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<CategoryStatisticsResponse> getCategoryStatistics() {
        Map<ReviewCategory, CategoryStatisticsResponse> statistics = new EnumMap<>(ReviewCategory.class);
        for (ReviewCategory category : ReviewCategory.values()) {
            statistics.put(category, new CategoryStatisticsResponse(category, 0, null));
        }
        for (CategoryTotals totals : reviewRepository.findCategoryTotals()) {
            statistics.put(totals.getCategory(), new CategoryStatisticsResponse(
                    totals.getCategory(), totals.getReviewCount(),
                    average(totals.getRatingSum(), totals.getReviewCount())));
        }
        return List.copyOf(statistics.values());
    }

    @Override
    public OverallStatisticsResponse getOverallStatistics() {
        long ratingSum = 0;
        long reviewCount = 0;
        for (CategoryTotals totals : reviewRepository.findCategoryTotals()) {
            ratingSum += totals.getRatingSum();
            reviewCount += totals.getReviewCount();
        }
        return new OverallStatisticsResponse(reviewCount, average(ratingSum, reviewCount));
    }

    @Override
    public List<RatingCount> getDistribution(ReviewCategory category) {
        Map<Integer, Long> counts = new HashMap<>();
        for (RatingCount row : reviewRepository.findRatingCounts(category)) {
            counts.put(row.rating(), row.count());
        }
        List<RatingCount> distribution = new ArrayList<>();
        for (int rating = 1; rating <= 5; rating++) {
            distribution.add(new RatingCount(rating, counts.getOrDefault(rating, 0L)));
        }
        return distribution;
    }

    private BigDecimal average(long ratingSum, long reviewCount) {
        if (reviewCount == 0) {
            return null;
        }
        return BigDecimal.valueOf(ratingSum).divide(BigDecimal.valueOf(reviewCount), 2, RoundingMode.HALF_UP);
    }
}
