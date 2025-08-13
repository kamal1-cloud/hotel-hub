package com.hotelhub.service;

import com.hotelhub.dto.HotelDto;
import com.hotelhub.dto.PagedResult;
import com.hotelhub.repository.HotelRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@ApplicationScoped
public class HotelSearchService {

    @Inject
    HotelRepository hotelRepository;

    public PagedResult<HotelDto> searchHotels(String query, int page, int size) {
        if (query == null || query.trim().isEmpty()) {
                        return getHotelsWithFilters(page, size, null, null, (BigDecimal) null, (BigDecimal) null, null, null);
        }
        return hotelRepository.searchHotels(query, page, size);
    }

    public PagedResult<HotelDto> getHotelsWithFilters(int page, int size, String city, String countryCode,
                                                      BigDecimal minRating, BigDecimal maxRating, Integer minStars, Integer maxStars) {
        return hotelRepository.findWithFilters(page, size, city, countryCode, minRating, maxRating, minStars, maxStars);
    }
}
