package com.hotelhub.mapper;

import com.hotelhub.dto.HotelRoomAmenityDto;
import com.hotelhub.entity.HotelRoomAmenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelRoomAmenityMapper {
    HotelRoomAmenityDto toDto(HotelRoomAmenity amenity);
    HotelRoomAmenity toEntity(HotelRoomAmenityDto dto);
}