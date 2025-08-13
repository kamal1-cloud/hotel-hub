package com.hotelhub.mapper;

import com.hotelhub.dto.HotelRoomViewDto;
import com.hotelhub.entity.HotelRoomView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelRoomViewMapper {
    HotelRoomViewDto toDto(HotelRoomView view);
    HotelRoomView toEntity(HotelRoomViewDto dto);
}