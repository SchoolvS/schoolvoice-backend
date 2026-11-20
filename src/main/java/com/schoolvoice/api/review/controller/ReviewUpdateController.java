package com.schoolvoice.api.review.controller;

import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.dto.ReviewUpdateResponse;
import com.schoolvoice.api.review.service.ReviewUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewUpdateController {

    private final ReviewUpdateService reviewUpdateService;

    public ReviewUpdateController(ReviewUpdateService reviewUpdateService) {
        this.reviewUpdateService = reviewUpdateService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @PathVariable("id") Long id,
            @RequestHeader("X-Edit-Token") String editToken,
            @Valid @RequestBody ReviewUpdateRequest request) {

        ReviewUpdateResponse response = reviewUpdateService.updateReview(id, editToken, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("id") Long id,
            @RequestHeader("X-Edit-Token") String editToken) {

        reviewUpdateService.deleteReview(id, editToken);
        return ResponseEntity.noContent().build();
    }
}