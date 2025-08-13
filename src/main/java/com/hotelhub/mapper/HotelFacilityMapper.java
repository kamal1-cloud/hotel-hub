package com.hotelhub.mapper;

import com.hotelhub.dto.HotelFacilityDto;
import com.hotelhub.entity.HotelFacility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelFacilityMapper {
    HotelFacilityDto toDto(HotelFacility facility);
    HotelFacility toEntity(HotelFacilityDto dto);
}