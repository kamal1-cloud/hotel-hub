package com.hotelhub.mapper;

import com.hotelhub.dto.HotelRoomBedTypeDto;
import com.hotelhub.entity.HotelRoomBedType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelRoomBedTypeMapper {
    HotelRoomBedTypeDto toDto(HotelRoomBedType bedType);
    HotelRoomBedType toEntity(HotelRoomBedTypeDto dto);
}