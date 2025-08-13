package com.hotelhub.service;

import com.hotelhub.dto.HotelDto;
import com.hotelhub.dto.HotelRoomDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.mapper.HotelMapper;
import com.hotelhub.repository.HotelRepository;
import io.quarkus.cache.CacheInvalidate;
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
    HotelMapper hotelMapper;

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
        Hotel hotel = hotelRepository.findByIdWithRelations(id);
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
                    hotelRepository.persist(hotel);
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
        return hotelRepository.getHotelRooms(hotelId);
    }
}