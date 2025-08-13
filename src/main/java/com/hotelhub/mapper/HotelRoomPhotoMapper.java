package com.hotelhub.mapper;

import com.hotelhub.dto.HotelRoomPhotoDto;
import com.hotelhub.entity.HotelRoomPhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelRoomPhotoMapper {
    HotelRoomPhotoDto toDto(HotelRoomPhoto photo);
    HotelRoomPhoto toEntity(HotelRoomPhotoDto dto);
}