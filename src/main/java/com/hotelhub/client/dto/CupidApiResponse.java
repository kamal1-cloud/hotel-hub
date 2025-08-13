package com.hotelhub.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CupidApiResponse<T> {
    @JsonProperty("data")
    public T data;
}