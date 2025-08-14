package com.hotelhub.service;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.mapper.HotelMapper;
import com.hotelhub.repository.HotelFacilityRepository;
import com.hotelhub.repository.HotelPhotoRepository;
import com.hotelhub.repository.HotelPolicyRepository;
import com.hotelhub.repository.HotelRepository;
import com.hotelhub.repository.HotelReviewRepository;
import com.hotelhub.repository.HotelRoomRepository;
import com.hotelhub.repository.HotelTranslationRepository;
import io.quarkus.cache.CacheInvalidate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Specialized service for handling hotel data ingestion from external APIs.
 * Separates data ingestion concerns from core hotel business logic.
 */
@ApplicationScoped
public class HotelDataIngestionService {

    @Inject
    HotelRepository hotelRepository;

    @Inject
    HotelPhotoRepository hotelPhotoRepository;

    @Inject
    HotelFacilityRepository hotelFacilityRepository;

    @Inject
    HotelPolicyRepository hotelPolicyRepository;

    @Inject
    HotelRoomRepository hotelRoomRepository;

    @Inject
    HotelReviewRepository hotelReviewRepository;

    @Inject
    HotelTranslationRepository hotelTranslationRepository;

    @Inject
    HotelMapper hotelMapper;

    /**
     * Create hotel from Cupid API data
     */
    @Transactional
    @CacheInvalidate(cacheName = "hotel-by-id")
    public Hotel createFromCupidData(CupidPropertyDto cupidData) {
        Hotel hotel = hotelMapper.toEntity(cupidData);
        hotelRepository.persist(hotel);
        return hotel;
    }

    /**
     * Update existing hotel with Cupid API data
     */
    @Transactional
    @CacheInvalidate(cacheName = "hotel-by-id")
    public Hotel updateFromCupidData(Hotel hotel, CupidPropertyDto cupidData) {
        hotelMapper.updateEntityFromCupidDto(cupidData, hotel);
        return hotel;
    }

    /**
     * Update hotel with all related data from Cupid API
     */
    @CacheInvalidate(cacheName = "hotel-by-id")
    @Transactional
    public void updateHotelCompleteData(Hotel hotel, CupidPropertyDto propertyData,
                                        List<CupidReviewDto> reviews,
                                        CupidPropertyDto frenchData,
                                        CupidPropertyDto spanishData) {

        updateBasicHotelData(hotel, propertyData);
        Long hotelId = hotel.id;

        if (propertyData.photos != null && !propertyData.photos.isEmpty()) {
            updatePhotos(hotelId, propertyData.photos);
        }

        if (propertyData.facilities != null && !propertyData.facilities.isEmpty()) {
            updateFacilities(hotelId, propertyData.facilities);
        }

        if (propertyData.policies != null && !propertyData.policies.isEmpty()) {
            updatePolicies(hotelId, propertyData.policies);
        }

        if (propertyData.rooms != null && !propertyData.rooms.isEmpty()) {
            updateRooms(hotelId, propertyData.rooms);
        }

        if (reviews != null && !reviews.isEmpty()) {
            updateReviews(hotelId, reviews);
        }

        updateTranslations(hotelId, frenchData, spanishData);
    }


    private void updateBasicHotelData(Hotel hotel, CupidPropertyDto propertyData) {
        hotelMapper.updateEntityFromCupidDto(propertyData, hotel);
    }


    private void updatePhotos(Long hotelId, List<CupidPropertyDto.Photo> photos) {
        Hotel hotel = hotelRepository.findById(hotelId);
        hotelPhotoRepository.updateHotelPhotos(hotel, photos);
    }


    private void updateFacilities(Long hotelId, List<CupidPropertyDto.Facility> facilities) {
        Hotel hotel = hotelRepository.findById(hotelId);
        hotelFacilityRepository.updateHotelFacilities(hotel, facilities);
    }


    private void updatePolicies(Long hotelId, List<CupidPropertyDto.Policy> policies) {
        Hotel hotel = hotelRepository.findById(hotelId);
        hotelPolicyRepository.updateHotelPolicies(hotel, policies);
    }


    private void updateRooms(Long hotelId, List<CupidPropertyDto.Room> rooms) {
        Hotel hotel = hotelRepository.findById(hotelId);
        hotelRoomRepository.updateHotelRooms(hotel, rooms);
    }


    private void updateReviews(Long hotelId, List<CupidReviewDto> reviews) {
        Hotel hotel = hotelRepository.findById(hotelId);
        hotelReviewRepository.updateHotelReviews(hotel, reviews);
    }


    private void updateTranslations(Long hotelId, CupidPropertyDto frenchData, CupidPropertyDto spanishData) {
        Hotel hotel = hotelRepository.findById(hotelId);
        hotelTranslationRepository.updateHotelTranslations(hotel, frenchData, spanishData);
    }
}