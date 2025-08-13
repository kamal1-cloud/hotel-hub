package com.hotelhub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "cupid_id", unique = true, nullable = false)
    public Long cupidId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "markdown_description", columnDefinition = "TEXT")
    public String markdownDescription;

    @Column(name = "important_info", columnDefinition = "TEXT")
    public String importantInfo;

    // Address fields
    @Size(max = 255)
    @Column(name = "address")
    public String address;

    @Size(max = 100)
    @Column(name = "city")
    public String city;

    @Size(max = 100)
    @Column(name = "state")
    public String state;

    @Size(max = 2)
    @Column(name = "country_code")
    public String countryCode;

    @Size(max = 20)
    @Column(name = "postal_code")
    public String postalCode;

    // Contact information
    @Size(max = 50)
    @Column(name = "phone")
    public String phone;

    @Size(max = 50)
    @Column(name = "fax")
    public String fax;

    @Email
    @Size(max = 100)
    @Column(name = "email")
    public String email;

    // Hotel details
    @Min(0)
    @Max(5)
    @Column(name = "stars")
    public Integer stars;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    @Column(name = "rating", precision = 3, scale = 2)
    public BigDecimal rating;

    @Min(0)
    @Column(name = "review_count")
    public Integer reviewCount;

    @Size(max = 100)
    @Column(name = "hotel_type")
    public String hotelType;

    @Column(name = "hotel_type_id")
    public Integer hotelTypeId;

    @Size(max = 100)
    @Column(name = "chain")
    public String chain;

    @Column(name = "chain_id")
    public Integer chainId;

    // Geolocation
    @Column(name = "latitude", precision = 10, scale = 7)
    public BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    public BigDecimal longitude;

    @Size(max = 10)
    @Column(name = "airport_code")
    public String airportCode;

    // Images
    @Size(max = 500)
    @Column(name = "main_image_url")
    public String mainImageUrl;

    @Size(max = 500)
    @Column(name = "main_image_thumbnail")
    public String mainImageThumbnail;

    // Check-in/out information
    @Size(max = 20)
    @Column(name = "checkin_start")
    public String checkinStart;

    @Size(max = 20)
    @Column(name = "checkin_end")
    public String checkinEnd;

    @Size(max = 20)
    @Column(name = "checkout_time")
    public String checkoutTime;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    public String specialInstructions;

    @Size(max = 50)
    @Column(name = "parking")
    public String parking;

    @Column(name = "group_room_min")
    public Integer groupRoomMin;

    // Policy flags
    @Column(name = "child_allowed")
    public Boolean childAllowed;

    @Column(name = "pets_allowed")
    public Boolean petsAllowed;

    // Timestamps
    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "synced_at")
    public LocalDateTime syncedAt;

    // Relationships
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<HotelPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<HotelFacility> facilities = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<HotelPolicy> policies = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<HotelRoom> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<HotelReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<HotelTranslation> translations = new ArrayList<>();

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
