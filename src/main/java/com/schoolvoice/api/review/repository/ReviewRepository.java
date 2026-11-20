package com.schoolvoice.api.review.repository;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.stats.dto.RatingCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCategory(ReviewCategory category, Pageable pageable);
    long countByCategory(ReviewCategory category);
    long countByCategoryAndRating(ReviewCategory category, int rating);

    interface CategoryTotals {
        ReviewCategory getCategory();
        long getReviewCount();
        long getRatingSum();
    }

    @Query("""
            select r.category as category, count(r) as reviewCount, sum(r.rating) as ratingSum
            from Review r group by r.category
            """)
    List<CategoryTotals> findCategoryTotals();

    @Query("""
            select new com.schoolvoice.api.stats.dto.RatingCount(r.rating, count(r))
            from Review r where r.category = :category group by r.rating
            """)
    List<RatingCount> findRatingCounts(@Param("category") ReviewCategory category);
}
