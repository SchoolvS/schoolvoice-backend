package com.schoolvoice.api.review.controller;

import com.schoolvoice.api.review.dto.ReviewPageResponse;
import com.schoolvoice.api.review.dto.ReviewResponse;
import com.schoolvoice.api.review.dto.ReviewSort;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.service.ReviewReadService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewReadController {

    private final ReviewReadService reviewReadService;

    public ReviewReadController(ReviewReadService reviewReadService) {
        this.reviewReadService = reviewReadService;
    }

    @GetMapping
    public ResponseEntity<ReviewPageResponse> getReviews(
            @RequestParam(value = "category", required = false) ReviewCategory category,
            @RequestParam(value = "sort", defaultValue = "LATEST") ReviewSort sort,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        ReviewPageResponse response = reviewReadService.getReviews(category, sort, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable("id") Long id) {
        ReviewResponse response = reviewReadService.getReview(id);
        return ResponseEntity.ok(response);
    }
}
