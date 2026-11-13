package com.schoolvoice.api.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewCategory category;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Review() {
    }

    private Review(ReviewCategory category, int rating, String reason) {
        this.category = category;
        this.rating = rating;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Review create(ReviewCategory category, int rating, String reason) {
        return new Review(category, rating, reason);
    }

    public void update(int rating, String reason) {
        this.rating = rating;
        this.reason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ReviewCategory getCategory() {
        return category;
    }

    public int getRating() {
        return rating;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
