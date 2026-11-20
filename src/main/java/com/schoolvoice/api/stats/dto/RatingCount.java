package com.schoolvoice.api.stats.dto;

public record RatingCount(
  int rating,
  long count
) {
}
