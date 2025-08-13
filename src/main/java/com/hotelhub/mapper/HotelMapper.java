package com.hotelhub.mapper;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.dto.HotelDto;
import com.hotelhub.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "cdi", uses = {
    HotelPhotoMapper.class,
    HotelFacilityMapper.class,
    HotelReviewMapper.class,
    HotelTranslationMapper.class
})
public interface HotelMapper {
    
    @Mapping(target = "country", source = "countryCode")
    @Mapping(target = "photos", source = "photos")
    @Mapping(target = "facilities", source = "facilities")
    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "translations", source = "translations")
    HotelDto toDto(Hotel hotel);
    
    @Mapping(target = "countryCode", source = "country")
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "syncedAt", ignore = true)
    Hotel toEntity(HotelDto dto);
    
    @Mapping(target = "cupidId", source = "hotelId")
    @Mapping(target = "name", source = "hotelName")
    @Mapping(target = "address", source = "address.address")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "state", source = "address.state")
    @Mapping(target = "countryCode", source = "address.country")
    @Mapping(target = "postalCode", source = "address.postalCode")
    @Mapping(target = "latitude", source = "latitude", qualifiedByName = "mapLatitude")
    @Mapping(target = "longitude", source = "longitude", qualifiedByName = "mapLongitude")
    @Mapping(target = "mainImageUrl", source = "mainImageThumbnail")
    @Mapping(target = "checkinStart", source = "checkin.checkinStart")
    @Mapping(target = "checkinEnd", source = "checkin.checkinEnd")
    @Mapping(target = "checkoutTime", source = "checkin.checkout")
    @Mapping(target = "specialInstructions", source = "checkin.specialInstructions")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "syncedAt", ignore = true)
    @Mapping(target = "mainImageThumbnail", ignore = true)
    Hotel toEntity(CupidPropertyDto cupidDto);
    
    @Mapping(target = "cupidId", source = "hotelId")
    @Mapping(target = "name", source = "hotelName")
    @Mapping(target = "address", source = "address.address")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "state", source = "address.state")
    @Mapping(target = "countryCode", source = "address.country")
    @Mapping(target = "postalCode", source = "address.postalCode")
    @Mapping(target = "latitude", source = "latitude", qualifiedByName = "mapLatitude")
    @Mapping(target = "longitude", source = "longitude", qualifiedByName = "mapLongitude")
    @Mapping(target = "mainImageUrl", source = "mainImageThumbnail")
    @Mapping(target = "checkinStart", source = "checkin.checkinStart")
    @Mapping(target = "checkinEnd", source = "checkin.checkinEnd")
    @Mapping(target = "checkoutTime", source = "checkin.checkout")
    @Mapping(target = "specialInstructions", source = "checkin.specialInstructions")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "syncedAt", ignore = true)
    @Mapping(target = "mainImageThumbnail", ignore = true)
    void updateEntityFromCupidDto(CupidPropertyDto source, @MappingTarget Hotel target);
    
    @Mapping(target = "countryCode", source = "country")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "syncedAt", ignore = true)
    void updateEntityFromDto(HotelDto source, @MappingTarget Hotel target);
    
    // Helper methods for coordinate mapping
    @Named("mapLatitude")
    default BigDecimal mapLatitude(Double latitude) {
        return latitude != null ? BigDecimal.valueOf(latitude) : null;
    }
    
    @Named("mapLongitude")
    default BigDecimal mapLongitude(Double longitude) {
        return longitude != null ? BigDecimal.valueOf(longitude) : null;
    }
}