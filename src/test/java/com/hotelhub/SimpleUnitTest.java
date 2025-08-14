package com.hotelhub;

import java.math.BigDecimal;

import com.hotelhub.dto.HotelDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.mapper.HotelMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimpleUnitTest {

    @Inject
    HotelMapper hotelMapper;

    @Test
    public void testHotelMapperBasicMapping() {
        Hotel hotel = new Hotel();
        hotel.id = 1L;
        hotel.cupidId = 12345L;
        hotel.name = "Test Hotel";
        hotel.address = "123 Test St";
        hotel.city = "Test City";
        hotel.countryCode = "US";
        hotel.rating = BigDecimal.valueOf(4.5);

        HotelDto dto = hotelMapper.toDto(hotel);

        assertNotNull(dto);
        assertEquals(hotel.id, dto.id);
        assertEquals(hotel.cupidId, dto.cupidId);
        assertEquals(hotel.name, dto.name);
        assertEquals(hotel.address, dto.address);
        assertEquals(hotel.city, dto.city);
        assertEquals(hotel.countryCode, dto.country);
        assertEquals(hotel.rating, dto.rating);
    }

    @Test
    public void testDtoToEntityMapping() {
        HotelDto dto = new HotelDto();
        dto.cupidId = 54321L;
        dto.name = "DTO Hotel";
        dto.address = "456 DTO St";
        dto.city = "DTO City";
        dto.country = "CA";
        dto.rating = BigDecimal.valueOf(3.8);

        Hotel entity = hotelMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.cupidId, entity.cupidId);
        assertEquals(dto.name, entity.name);
        assertEquals(dto.address, entity.address);
        assertEquals(dto.city, entity.city);
        assertEquals(dto.country, entity.countryCode);
        assertEquals(dto.rating, entity.rating);
    }
}