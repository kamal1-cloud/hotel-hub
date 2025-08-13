package com.hotelhub.dto;

import java.util.Map;

public class HotelStatisticsDto {
    public long totalHotels;
    public Double averageRating;
    public Integer maxStars;
    public Integer minStars;
    public long totalReviews;
    public Map<String, Long> hotelsByCountry;
    public Map<String, Long> hotelsByCity;
    public Map<Integer, Long> hotelsByStars;
}