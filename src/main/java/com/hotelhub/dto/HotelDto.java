package com.hotelhub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class HotelDto {
    public Long id;
    public Long cupidId;
    public String name;
    public String address;
    public String city;
    public String state;
    public String country;
    public String postalCode;
    public BigDecimal rating;
    public Integer reviewCount;
    public Integer stars;
    public BigDecimal latitude;
    public BigDecimal longitude;
    public String airportCode;
    public String phone;
    public String fax;
    public String email;
    public String hotelType;
    public Integer hotelTypeId;
    public String chain;
    public Integer chainId;
    public String description;
    public String markdownDescription;
    public String importantInfo;
    public String mainImageUrl;
    public Boolean childAllowed;
    public Boolean petsAllowed;
    public String checkinStart;
    public String checkinEnd;
    public String checkoutTime;
    public String specialInstructions;
    public String parking;
    public Integer groupRoomMin;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    
    // Related data
    public List<HotelPhotoDto> photos;
    public List<HotelFacilityDto> facilities;
    public List<HotelReviewDto> reviews;
    public List<HotelTranslationDto> translations;
}
