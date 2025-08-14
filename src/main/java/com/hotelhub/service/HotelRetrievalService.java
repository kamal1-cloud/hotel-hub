package com.hotelhub.service;

import com.hotelhub.dto.*;
import com.hotelhub.entity.Hotel;
import com.hotelhub.repository.HotelRepository;
import com.hotelhub.repository.HotelReviewRepository;
import com.hotelhub.repository.HotelTranslationRepository;
import com.hotelhub.repository.HotelPhotoRepository;
import com.hotelhub.repository.HotelFacilityRepository;
import com.hotelhub.repository.HotelRoomRepository;
import com.hotelhub.repository.HotelPolicyRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class HotelRetrievalService {

    @Inject
    HotelRepository hotelRepository;

    @Inject
    HotelReviewRepository hotelReviewRepository;

    @Inject
    HotelTranslationRepository hotelTranslationRepository;

    @Inject
    HotelPhotoRepository hotelPhotoRepository;

    @Inject
    HotelFacilityRepository hotelFacilityRepository;

    @Inject
    HotelRoomRepository hotelRoomRepository;

    @Inject
    HotelPolicyRepository hotelPolicyRepository;

    @CacheResult(cacheName = "hotel-reviews")
    public PagedResult<HotelReviewDto> getHotelReviews(Long hotelId, int page, int size) {
        // Validate hotel exists first
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return new PagedResult<>(List.of(), page, size, 0);
        }
        return hotelReviewRepository.getHotelReviews(hotelId, page, size);
    }

    @CacheResult(cacheName = "hotel-translations")
    public List<HotelTranslationDto> getHotelTranslations(Long hotelId, String language) {
        // Validate hotel exists first
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return List.of();
        }
        return hotelTranslationRepository.getHotelTranslations(hotelId, language);
    }

    @CacheResult(cacheName = "hotel-photos")
    public List<HotelPhotoDto> getHotelPhotos(Long hotelId) {
        // Validate hotel exists first
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return List.of();
        }
        return hotelPhotoRepository.getHotelPhotos(hotelId);
    }

    @CacheResult(cacheName = "hotel-facilities")
    public List<HotelFacilityDto> getHotelFacilities(Long hotelId) {
        // Validate hotel exists first
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return List.of();
        }
        return hotelFacilityRepository.getHotelFacilities(hotelId);
    }

    @CacheResult(cacheName = "hotel-rooms")
    public List<HotelRoomDto> getHotelRooms(Long hotelId) {
        // Validate hotel exists first
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return List.of();
        }
        return hotelRoomRepository.getHotelRooms(hotelId);
    }

    @CacheResult(cacheName = "hotel-statistics")
    public HotelStatisticsDto getHotelStatistics() {
        var stats = hotelRepository.getHotelStatistics();
        // Set the total reviews count from the review repository
        stats.totalReviews = hotelReviewRepository.getTotalReviewsCount();
        return stats;
    }

    public Hotel findByIdWithRelations(Long hotelId) {
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return null;
        }

        // Eagerly initialize all relations to avoid LazyInitializationException
        // This ensures the Hotel entity can be safely used outside transaction boundaries
        hotel.photos.size();
        hotel.facilities.size();
        hotel.reviews.size();
        hotel.rooms.size();
        hotel.translations.size();
        hotel.policies.size();
        
        // Also initialize nested collections in rooms
        for (var room : hotel.rooms) {
            room.bedTypes.size();
            room.amenities.size();
            room.photos.size();
            room.views.size();
        }
        
        return hotel;
    }
}
