package com.hotelhub.dto;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public class HotelReviewDto {
    public Long id;
    public String cupidReviewId;
    public BigDecimal averageScore;
    public String country;
    public String type;
    public String name;
    public String date;
    public String headline;
    public String language;
    public String pros;
    public String cons;
    public String source;
    public LocalDateTime createdAt;
}