package com.schoolvoice.api.review.dto;

import com.schoolvoice.api.review.entity.ReviewCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(
        @NotNull ReviewCategory category,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotBlank @Size(min = 10, max = 1000) String reason
) {
}
