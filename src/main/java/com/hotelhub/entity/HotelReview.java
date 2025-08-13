package com.hotelhub.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_reviews")
public class HotelReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "cupid_review_id", unique = true)
    public String cupidReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference
    public Hotel hotel;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    @Column(name = "average_score", precision = 3, scale = 2)
    public BigDecimal averageScore;

    @Size(max = 100)
    @Column(name = "country")
    public String country;

    @Size(max = 100)
    @Column(name = "type")
    public String type;

    @Size(max = 255)
    @Column(name = "name")
    public String name;

    @Column(name = "date")
    public String date;

    @Size(max = 255)
    @Column(name = "headline")
    public String headline;

    @Column(name = "pros", columnDefinition = "TEXT")
    public String pros;

    @Column(name = "cons", columnDefinition = "TEXT")
    public String cons;

    @Size(max = 100)
    @Column(name = "source")
    public String source;

    @Size(max = 10)
    @Column(name = "language")
    public String language;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}