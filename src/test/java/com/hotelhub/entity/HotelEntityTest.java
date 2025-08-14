package com.hotelhub.entity;

import java.math.BigDecimal;

import com.hotelhub.repository.HotelRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(TestEntityProfile.class)
public class HotelEntityTest {

    @Inject
    Validator validator;

    @Inject
    HotelRepository hotelRepository;


    @Test
    @io.quarkus.test.TestTransaction
    public void testCreateValidHotel() {
        Hotel hotel = new Hotel();
        hotel.cupidId = 12345L;
        hotel.name = "Test Hotel";
        hotel.address = "123 Test St";
        hotel.city = "Test City";
        hotel.countryCode = "US";
        hotel.rating = BigDecimal.valueOf(4.5);
        hotel.stars = 4;

        hotelRepository.persistAndFlush(hotel); // ensures @PrePersist runs

        assertNotNull(hotel.id);
        assertNotNull(hotel.createdAt);
        assertNotNull(hotel.updatedAt);
    }

    @Test
    public void testHotelValidation() {
        Hotel hotel = new Hotel(); // missing required fields

        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);
        assertFalse(violations.isEmpty());

        boolean hasNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(hasNameViolation);
    }

    @Test
    @io.quarkus.test.TestTransaction
    public void testFindByCupidId() {
        Hotel hotel = new Hotel();
        hotel.cupidId = 54321L;
        hotel.name = "Find Me Hotel";
        hotelRepository.persistAndFlush(hotel);

        Hotel found = hotelRepository.findByCupidId(54321L);
        assertNotNull(found);
        assertEquals("Find Me Hotel", found.name);

        Hotel notFound = hotelRepository.findByCupidId(99999L);
        assertNull(notFound);
    }

    @Test
    @io.quarkus.test.TestTransaction
    public void testHotelLifecycleMethods() {
        Hotel hotel = new Hotel();
        hotel.cupidId = 11111L;
        hotel.name = "Lifecycle Test Hotel";

        hotelRepository.persistAndFlush(hotel);

        assertNotNull(hotel.createdAt);
        assertNotNull(hotel.updatedAt);

        // Allow tiny timing differences from two separate now() calls
        long diffMillis = Math.abs(Duration.between(hotel.createdAt, hotel.updatedAt).toMillis());
        assertTrue(diffMillis <= 1000, "createdAt and updatedAt should be ~equal on first persist");

        var originalCreatedAt = hotel.createdAt;
        var originalUpdatedAt = hotel.updatedAt;


        hotel.name = "Updated Hotel Name";
        hotelRepository.flush(); // triggers @PreUpdate

        assertEquals(originalCreatedAt, hotel.createdAt, "createdAt must not change on update");
        assertTrue(hotel.updatedAt.isAfter(originalUpdatedAt), "updatedAt must advance on update");
    }

    @Test
    public void testHotelRatingConstraints() {
        Hotel hotel = new Hotel();
        hotel.cupidId = 22222L;
        hotel.name = "Rating Test Hotel";
        hotel.rating = BigDecimal.valueOf(15.0); // invalid (>10)

        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);
        boolean hasRatingViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("rating"));
        assertTrue(hasRatingViolation);

        hotel.rating = BigDecimal.valueOf(4.5); // valid
        violations = validator.validate(hotel);
        boolean hasValidRating = violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("rating"));
        assertTrue(hasValidRating);
    }

    @Test
    public void testHotelStringLengthConstraints() {
        Hotel hotel = new Hotel();
        hotel.cupidId = 33333L;
        hotel.name = "A".repeat(256); // too long (max 255)

        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);
        boolean hasNameLengthViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(hasNameLengthViolation);
    }
}