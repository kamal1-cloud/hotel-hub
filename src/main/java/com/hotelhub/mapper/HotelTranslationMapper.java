package com.hotelhub.mapper;

import com.hotelhub.dto.HotelTranslationDto;
import com.hotelhub.entity.HotelTranslation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelTranslationMapper {
    HotelTranslationDto toDto(HotelTranslation translation);
    HotelTranslation toEntity(HotelTranslationDto dto);
}