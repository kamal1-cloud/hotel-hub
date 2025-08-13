package com.hotelhub.service;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.dto.HotelDto;
import com.hotelhub.dto.HotelRoomDto;
import com.hotelhub.entity.Hotel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * Main hotel service that delegates to specialized services.
 * Follows Single Responsibility Principle and clean architecture.
 * This is a facade/coordinator service that delegates to more focused services.
 */
@ApplicationScoped
public class HotelService {

    @Inject
    HotelCoreService hotelCoreService;

    @Inject
    HotelDataIngestionService dataIngestionService;

    // Core CRUD operations
    
    public HotelDto createHotel(HotelDto hotelDto) {
        return hotelCoreService.createHotel(hotelDto);
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelCoreService.createHotel(hotel);
    }

    public List<HotelDto> getAllHotels() {
        return hotelCoreService.getAllHotels();
    }

    public Optional<HotelDto> getHotelById(Long id) {
        return hotelCoreService.getHotelById(id);
    }

    public Optional<HotelDto> updateHotel(Long id, HotelDto hotelDto) {
        return hotelCoreService.updateHotel(id, hotelDto);
    }

    public boolean deleteHotel(Long id) {
        return hotelCoreService.deleteHotel(id);
    }

    public Hotel findByCupidId(Long cupidId) {
        return hotelCoreService.findByCupidId(cupidId);
    }

    public List<HotelRoomDto> getHotelRooms(Long hotelId) {
        return hotelCoreService.getHotelRooms(hotelId);
    }

    // Data ingestion operations
    
    public Hotel createFromCupidData(CupidPropertyDto cupidData) {
        return dataIngestionService.createFromCupidData(cupidData);
    }

    public Hotel updateFromCupidData(Hotel hotel, CupidPropertyDto cupidData) {
        return dataIngestionService.updateFromCupidData(hotel, cupidData);
    }

    public void updateHotelCompleteData(Hotel hotel, CupidPropertyDto propertyData, 
                                       List<CupidReviewDto> reviews,
                                       CupidPropertyDto frenchData, 
                                       CupidPropertyDto spanishData) {
        dataIngestionService.updateHotelCompleteData(hotel, propertyData, reviews, frenchData, spanishData);
    }
}