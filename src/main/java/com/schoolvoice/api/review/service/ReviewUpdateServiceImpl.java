package com.schoolvoice.api.review.service;

import com.schoolvoice.api.review.dto.ReviewUpdateRequest;
import com.schoolvoice.api.review.dto.ReviewUpdateResponse;
import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@Transactional
public class ReviewUpdateServiceImpl implements ReviewUpdateService {

    private final ReviewRepository reviewRepository;

    public ReviewUpdateServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewUpdateResponse updateReview(Long id, String editToken, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."));

        verifyEditToken(review.getHashedEditToken(), editToken);

        review.update(request.rating(), request.reason());

        return ReviewUpdateResponse.from(review);
    }

    @Override
    public void deleteReview(Long id, String editToken) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."));

        verifyEditToken(review.getHashedEditToken(), editToken);

        reviewRepository.delete(review);
    }

    private void verifyEditToken(String savedHashedToken, String rawEditToken) {
        if (rawEditToken == null || rawEditToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }
        String hashedInputToken = hashToken(rawEditToken);
        if (!savedHashedToken.equals(hashedInputToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }
    }

    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해시 알고리즘 오류", e);
        }
    }
}