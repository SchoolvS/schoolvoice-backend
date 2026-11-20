package com.schoolvoice.api.review.controller;

import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.dto.ReviewUpdateResponse;
import com.schoolvoice.api.review.service.ReviewCommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewCommandController {

    private final ReviewCommandService reviewCommandService;

    public ReviewCommandController(ReviewCommandService reviewCommandService) {
        this.reviewCommandService = reviewCommandService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-Edit-Token", required = false) String editToken,
            @Valid @RequestBody ReviewUpdateRequest request) {

        ReviewUpdateResponse response = reviewCommandService.updateReview(id, editToken, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-Edit-Token", required = false) String editToken) {

        reviewCommandService.deleteReview(id, editToken);

        return ResponseEntity.noContent().build();
    }
}