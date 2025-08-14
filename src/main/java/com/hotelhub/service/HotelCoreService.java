package com.hotelhub.service;

import com.hotelhub.dto.HotelDto;
import com.hotelhub.dto.HotelRoomDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.mapper.HotelMapper;
import com.hotelhub.repository.HotelRepository;
import com.hotelhub.repository.HotelRoomRepository;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Core hotel service focusing on basic CRUD operations and business logic.
 * Follows Single Responsibility Principle and clean layered architecture.
 */
@ApplicationScoped
public class HotelCoreService {

    @Inject
    HotelRepository hotelRepository;

    @Inject
    HotelRoomRepository hotelRoomRepository;

    @Inject
    HotelMapper hotelMapper;

    @Inject
    HotelRetrievalService hotelRetrievalService;

    /**
     * Create a new hotel from DTO
     */
    @Transactional
    public HotelDto createHotel(HotelDto hotelDto) {
        Hotel hotel = hotelMapper.toEntity(hotelDto);
        hotelRepository.persist(hotel);
        return hotelMapper.toDto(hotel);
    }

    /**
     * Create a new hotel entity (for internal use)
     */
    @Transactional
    public Hotel createHotel(Hotel hotel) {
        hotelRepository.persistAndFlush(hotel);
        return hotel;
    }

    /**
     * Get all hotels as DTOs
     */
    public List<HotelDto> getAllHotels() {
        return hotelRepository.listAll().stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get hotel by ID with caching
     */
    @CacheResult(cacheName = "hotel-by-id")
    public Optional<HotelDto> getHotelById(@CacheKey Long id) {
        Hotel hotel = hotelRetrievalService.findByIdWithRelations(id);
        return hotel != null ? Optional.of(hotelMapper.toDto(hotel)) : Optional.empty();
    }

    /**
     * Update hotel with cache invalidation
     */
    @Transactional
    @CacheInvalidate(cacheName = "hotel-by-id")
    public Optional<HotelDto> updateHotel(@CacheKey Long id, HotelDto hotelDto) {
        return hotelRepository.findByIdOptional(id)
                .map(hotel -> {
                    hotelMapper.updateEntityFromDto(hotelDto, hotel);
                    hotelRepository.flush();
                    return hotelMapper.toDto(hotel);
                });
    }

    /**
     * Delete hotel with cache invalidation
     */
    @Transactional
    @CacheInvalidate(cacheName = "hotel-by-id")
    public boolean deleteHotel(@CacheKey Long id) {
        return hotelRepository.deleteById(id);
    }

    @Transactional
    @CacheInvalidateAll(cacheName = "hotel-by-id")
    public void deleteAllHotels() {
        hotelRepository.deleteAll();
    }

    /**
     * Find hotel by Cupid ID
     */
    public Hotel findByCupidId(Long cupidId) {
        return hotelRepository.findByCupidId(cupidId);
    }

    /**
     * Get hotel rooms by hotel ID
     */
    public List<HotelRoomDto> getHotelRooms(Long hotelId) {
        // Validate hotel exists first
        var hotel = hotelRepository.findById(hotelId);
        if (hotel == null) {
            return List.of();
        }
        return hotelRoomRepository.getHotelRooms(hotelId);
    }
}