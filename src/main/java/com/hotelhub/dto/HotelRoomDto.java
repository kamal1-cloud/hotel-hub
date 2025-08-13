package com.hotelhub.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HotelRoomDto {
    public Long id;
    public Long cupidRoomId;
    public String roomName;
    public String description;
    public Integer roomSizeSquare;
    public String roomSizeUnit;
    public Integer maxAdults;
    public Integer maxChildren;
    public Integer maxOccupancy;
    public String bedRelation;
    public List<HotelRoomBedTypeDto> bedTypes;
    public List<HotelRoomAmenityDto> amenities;
    public List<HotelRoomPhotoDto> photos;
    public List<HotelRoomViewDto> views;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}