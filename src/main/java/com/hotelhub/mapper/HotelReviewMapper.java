package com.hotelhub.mapper;

import com.hotelhub.dto.HotelReviewDto;
import com.hotelhub.entity.HotelReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HotelReviewMapper {
    HotelReviewDto toDto(HotelReview review);
    HotelReview toEntity(HotelReviewDto dto);
}