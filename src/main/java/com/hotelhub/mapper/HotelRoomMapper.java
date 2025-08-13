package com.hotelhub.mapper;

import com.hotelhub.dto.HotelRoomDto;
import com.hotelhub.entity.HotelRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = {
    HotelRoomBedTypeMapper.class,
    HotelRoomAmenityMapper.class,
    HotelRoomPhotoMapper.class,
    HotelRoomViewMapper.class
})
public interface HotelRoomMapper {
    @Mapping(target = "bedTypes", source = "bedTypes")
    @Mapping(target = "amenities", source = "amenities")
    @Mapping(target = "photos", source = "photos")
    @Mapping(target = "views", source = "views")
    HotelRoomDto toDto(HotelRoom room);
    
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "bedTypes", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    HotelRoom toEntity(HotelRoomDto dto);
}