package com.hotelhub.mapper;

import com.hotelhub.dto.HotelPhotoDto;
import com.hotelhub.entity.HotelPhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelPhotoMapper {
    HotelPhotoDto toDto(HotelPhoto photo);
    HotelPhoto toEntity(HotelPhotoDto dto);
}