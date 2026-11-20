package com.schoolvoice.api.stats.dto;

import java.math.BigDecimal;

public record OverallStatisticsResponse(
  long reviewCount,
  BigDecimal averageRating
) {
}
