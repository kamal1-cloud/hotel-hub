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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_room_photos")
public class HotelRoomPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    public HotelRoom room;

    @NotBlank
    @Size(max = 500)
    @Column(name = "url", nullable = false)
    public String url;

    @Size(max = 500)
    @Column(name = "hd_url")
    public String hdUrl;

    @Size(max = 255)
    @Column(name = "image_description")
    public String imageDescription;

    @Size(max = 100)
    @Column(name = "image_class1")
    public String imageClass1;

    @Size(max = 100)
    @Column(name = "image_class2")
    public String imageClass2;

    @Column(name = "main_photo")
    public Boolean mainPhoto;

    @Column(name = "score", precision = 4, scale = 2)
    public BigDecimal score;

    @Column(name = "class_id")
    public Integer classId;

    @Column(name = "class_order")
    public Integer classOrder;

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