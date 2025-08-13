package com.hotelhub.service;

import com.hotelhub.dto.*;
import com.hotelhub.repository.HotelRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class HotelRetrievalService {

    @Inject
    HotelRepository hotelRepository;

    @CacheResult(cacheName = "hotel-reviews")
    public PagedResult<HotelReviewDto> getHotelReviews(Long hotelId, int page, int size) {
        return hotelRepository.getHotelReviews(hotelId, page, size);
    }

    @CacheResult(cacheName = "hotel-translations")
    public List<HotelTranslationDto> getHotelTranslations(Long hotelId, String language) {
        return hotelRepository.getHotelTranslations(hotelId, language);
    }

    @CacheResult(cacheName = "hotel-photos")
    public List<HotelPhotoDto> getHotelPhotos(Long hotelId) {
        return hotelRepository.getHotelPhotos(hotelId);
    }

    @CacheResult(cacheName = "hotel-facilities")
    public List<HotelFacilityDto> getHotelFacilities(Long hotelId) {
        return hotelRepository.getHotelFacilities(hotelId);
    }

    @CacheResult(cacheName = "hotel-statistics")
    public HotelStatisticsDto getHotelStatistics() {
        return hotelRepository.getHotelStatistics();
    }
}
