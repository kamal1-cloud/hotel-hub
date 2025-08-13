package com.hotelhub.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CupidReviewDto {

    @JsonProperty("review_id")
    public String reviewId;

    @JsonProperty("average_score")
    public Double averageScore;

    @JsonProperty("country")
    public String country;

    @JsonProperty("type")
    public String type;

    @JsonProperty("name")
    public String name;

    @JsonProperty("date")
    public String date;

    @JsonProperty("headline")
    public String headline;

    @JsonProperty("language")
    public String language;

    @JsonProperty("pros")
    public String pros;

    @JsonProperty("cons")
    public String cons;

    @JsonProperty("source")
    public String source;
}