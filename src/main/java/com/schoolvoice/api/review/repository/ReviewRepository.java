package com.schoolvoice.api.review.repository;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCategory(ReviewCategory category, Pageable pageable);
    long countByCategory(ReviewCategory category);
    long countByCategoryAndRating(ReviewCategory category, int rating);
}