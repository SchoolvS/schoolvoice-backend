package com.schoolvoice.api.stats.dto;

import com.schoolvoice.api.review.entity.ReviewCategory;

import java.math.BigDecimal;

public record CategoryStatisticsResponse(
  ReviewCategory category,
  long reviewCount,
  BigDecimal averageRating
) {
}
