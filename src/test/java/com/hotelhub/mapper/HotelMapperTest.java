package com.hotelhub.mapper;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.dto.HotelDto;
import com.hotelhub.entity.Hotel;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
public class HotelMapperTest {

    @Inject
    HotelMapper hotelMapper;

    @Test
    public void testToDto() {
        Hotel hotel = new Hotel();
        hotel.id = 1L;
        hotel.cupidId = 12345L;
        hotel.name = "Test Hotel";
        hotel.address = "123 Test St";
        hotel.city = "Test City";
        hotel.countryCode = "US";
        hotel.rating = BigDecimal.valueOf(4.5);
        hotel.stars = 4;
        hotel.latitude = BigDecimal.valueOf(40.7128);
        hotel.longitude = BigDecimal.valueOf(-74.0060);
        hotel.phone = "+1-555-123-4567";
        hotel.email = "test@hotel.com";
        hotel.description = "A great test hotel";

        HotelDto dto = hotelMapper.toDto(hotel);

        assertNotNull(dto);
        assertEquals(hotel.id, dto.id);
        assertEquals(hotel.cupidId, dto.cupidId);
        assertEquals(hotel.name, dto.name);
        assertEquals(hotel.address, dto.address);
        assertEquals(hotel.city, dto.city);
        assertEquals(hotel.countryCode, dto.country);
        assertEquals(hotel.rating, dto.rating);
        assertEquals(hotel.stars, dto.stars);
        assertEquals(hotel.latitude, dto.latitude);
        assertEquals(hotel.longitude, dto.longitude);
        assertEquals(hotel.phone, dto.phone);
        assertEquals(hotel.email, dto.email);
        assertEquals(hotel.description, dto.description);
    }

    @Test
    public void testToDtoWithNullValues() {
        Hotel hotel = new Hotel();
        hotel.id = 1L;
        hotel.name = "Minimal Hotel";
        // Other fields are null

        HotelDto dto = hotelMapper.toDto(hotel);

        assertNotNull(dto);
        assertEquals(hotel.id, dto.id);
        assertEquals(hotel.name, dto.name);
        assertNull(dto.address);
        assertNull(dto.city);
        assertNull(dto.rating);
    }

    @Test
    public void testToEntity() {
        HotelDto dto = new HotelDto();
        dto.cupidId = 54321L;
        dto.name = "DTO Hotel";
        dto.address = "456 DTO St";
        dto.city = "DTO City";
        dto.country = "CA";
        dto.rating = BigDecimal.valueOf(3.8);
        dto.stars = 3;
        dto.latitude = BigDecimal.valueOf(43.6532);
        dto.longitude = BigDecimal.valueOf(-79.3832);
        dto.phone = "+1-555-987-6543";
        dto.email = "dto@hotel.com";
        dto.description = "A DTO test hotel";

        Hotel entity = hotelMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.cupidId, entity.cupidId);
        assertEquals(dto.name, entity.name);
        assertEquals(dto.address, entity.address);
        assertEquals(dto.city, entity.city);
        assertEquals(dto.country, entity.countryCode);
        assertEquals(dto.rating, entity.rating);
        assertEquals(dto.stars, entity.stars);
        assertEquals(dto.latitude, entity.latitude);
        assertEquals(dto.longitude, entity.longitude);
        assertEquals(dto.phone, entity.phone);
        assertEquals(dto.email, entity.email);
        assertEquals(dto.description, entity.description);
    }

    @Test
    public void testToEntityFromCupidPropertyDto() {
        CupidPropertyDto cupidDto = new CupidPropertyDto();
        cupidDto.hotelId = 98765L;
        cupidDto.hotelName = "Cupid Hotel";
        cupidDto.rating = BigDecimal.valueOf(4.2);
        cupidDto.stars = 4;
        cupidDto.description = "A Cupid API hotel";
        cupidDto.phone = "+1-555-111-2222";
        cupidDto.email = "cupid@hotel.com";
        cupidDto.latitude = 37.7749;
        cupidDto.longitude = -122.4194;

        cupidDto.address = new CupidPropertyDto.Address();
        cupidDto.address.address = "789 Cupid Ave";
        cupidDto.address.city = "Cupid City";
        cupidDto.address.country = "US";
        cupidDto.address.state = "CA";
        cupidDto.address.postalCode = "12345";

        Hotel entity = hotelMapper.toEntity(cupidDto);

        assertNotNull(entity);
        assertEquals(cupidDto.hotelId, entity.cupidId);
        assertEquals(cupidDto.hotelName, entity.name);
        assertEquals(cupidDto.rating, entity.rating);
        assertEquals(cupidDto.stars, entity.stars);
        assertEquals(cupidDto.description, entity.description);
        assertEquals(cupidDto.phone, entity.phone);
        assertEquals(cupidDto.email, entity.email);
        assertEquals(BigDecimal.valueOf(cupidDto.latitude), entity.latitude);
        assertEquals(BigDecimal.valueOf(cupidDto.longitude), entity.longitude);
        assertEquals(cupidDto.address.address, entity.address);
        assertEquals(cupidDto.address.city, entity.city);
        assertEquals(cupidDto.address.country, entity.countryCode);
        assertEquals(cupidDto.address.state, entity.state);
        assertEquals(cupidDto.address.postalCode, entity.postalCode);
    }

    @Test
    public void testToEntityFromCupidPropertyDtoWithNullAddress() {
        CupidPropertyDto cupidDto = new CupidPropertyDto();
        cupidDto.hotelId = 11111L;
        cupidDto.hotelName = "Minimal Cupid Hotel";
        cupidDto.rating = BigDecimal.valueOf(4.0);
        // address is null

        Hotel entity = hotelMapper.toEntity(cupidDto);

        assertNotNull(entity);
        assertEquals(cupidDto.hotelId, entity.cupidId);
        assertEquals(cupidDto.hotelName, entity.name);
        assertEquals(cupidDto.rating, entity.rating);
        assertNull(entity.address);
        assertNull(entity.city);
        assertNull(entity.countryCode);
    }

    @Test
    public void testNullInput() {
        assertNull(hotelMapper.toDto(null));
        assertNull(hotelMapper.toEntity((HotelDto) null));
        assertNull(hotelMapper.toEntity((CupidPropertyDto) null));
    }
}