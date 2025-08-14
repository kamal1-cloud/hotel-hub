package com.hotelhub.service;

import com.hotelhub.client.CupidApiClient;
import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelReview;
import com.hotelhub.entity.HotelTranslation;
import com.hotelhub.repository.HotelRepository;
import com.hotelhub.repository.HotelReviewRepository;
import com.hotelhub.repository.HotelTranslationRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
@TestProfile(TestServiceProfile.class)
public class DataIngestionServiceTest {

    @Inject
    DataIngestionService dataIngestionService;

    @Inject
    HotelRepository hotelRepository;

    @Inject
    HotelReviewRepository hotelReviewRepository;

    @Inject
    HotelTranslationRepository hotelTranslationRepository;

    @InjectMock
    @org.eclipse.microprofile.rest.client.inject.RestClient
    CupidApiClient cupidApiClient;

    @BeforeEach
    @Transactional
    void setup() {
        // Clean up
        hotelReviewRepository.deleteAll();
        hotelTranslationRepository.deleteAll();
        hotelRepository.deleteAll();

        // Setup mock data
        CupidPropertyDto propertyDto = new CupidPropertyDto();
        propertyDto.hotelId = 123L;
        propertyDto.hotelName = "Mock Hotel";
        propertyDto.rating = BigDecimal.valueOf(4.0);
        propertyDto.stars = 4;
        propertyDto.description = "A great hotel";
        propertyDto.address = new CupidPropertyDto.Address();
        propertyDto.address.address = "123 Mock St";
        propertyDto.address.city = "Mock City";
        propertyDto.address.country = "US";

        CupidReviewDto reviewDto = new CupidReviewDto();
        reviewDto.reviewId = "review123";
        reviewDto.averageScore = 4.5;
        reviewDto.headline = "Great stay!";
        reviewDto.pros = "Clean room";
        reviewDto.cons = "Noisy";
        reviewDto.country = "US";
        reviewDto.type = "business";
        reviewDto.name = "John Doe";
        reviewDto.date = "2024-01-15";
        reviewDto.language = "en";
        reviewDto.source = "booking.com";

        List<CupidReviewDto> reviewsList = Collections.singletonList(reviewDto);

        CupidPropertyDto frProperty = new CupidPropertyDto();
        frProperty.hotelName = "Hôtel Maquette";
        frProperty.description = "Un excellent hôtel";
        frProperty.address = new CupidPropertyDto.Address();
        frProperty.address.address = "123 Rue Maquette";

        CupidPropertyDto esProperty = new CupidPropertyDto();
        esProperty.hotelName = "Hotel Maqueta";
        esProperty.description = "Un hotel excelente";
        esProperty.address = new CupidPropertyDto.Address();
        esProperty.address.address = "123 Calle Maqueta";

        // Mock API calls (now returning data directly)
        Mockito.when(cupidApiClient.getPropertyById(123L)).thenReturn(propertyDto);
        Mockito.when(cupidApiClient.getReviewsByPropertyId(123L)).thenReturn(reviewsList);
        Mockito.when(cupidApiClient.getTranslationByPropertyIdAndLanguage(123L, "fr")).thenReturn(frProperty);
        Mockito.when(cupidApiClient.getTranslationByPropertyIdAndLanguage(123L, "es")).thenReturn(esProperty);
    }

    @Test
    @Transactional
    void testIngestHotelDataNewHotel() {
        List<Long> hotelIds = Collections.singletonList(123L);

        dataIngestionService.ingestHotelData(hotelIds);

        // Verify hotel was created
        Hotel hotel = hotelRepository.findByCupidId(123L);
        assertNotNull(hotel);
        assertEquals("Mock Hotel", hotel.name);
        assertEquals("Mock City", hotel.city);
        assertEquals("US", hotel.countryCode);
        assertEquals(BigDecimal.valueOf(4.0), hotel.rating);
        assertEquals(Integer.valueOf(4), hotel.stars);

        // Verify review was created
        List<HotelReview> reviews = hotelReviewRepository.findByHotel(hotel);
        assertEquals(1, reviews.size());
        HotelReview review = reviews.get(0);
        assertEquals("review123", review.cupidReviewId);
        assertEquals(BigDecimal.valueOf(4.5), review.averageScore);
        assertEquals("Great stay!", review.headline);

        // Verify translations were created
        List<HotelTranslation> translations = hotelTranslationRepository.findByHotel(hotel);
        assertEquals(2, translations.size());

        HotelTranslation frTranslation = translations.stream()
                .filter(t -> "fr".equals(t.language))
                .findFirst()
                .orElse(null);
        assertNotNull(frTranslation);
        assertEquals("Hôtel Maquette", frTranslation.translatedName);
        assertEquals("Un excellent hôtel", frTranslation.translatedDescription);

        HotelTranslation esTranslation = translations.stream()
                .filter(t -> "es".equals(t.language))
                .findFirst()
                .orElse(null);
        assertNotNull(esTranslation);
        assertEquals("Hotel Maqueta", esTranslation.translatedName);
        assertEquals("Un hotel excelente", esTranslation.translatedDescription);
    }

    @Test
    @Transactional
    void testIngestHotelDataExistingHotel() {
        // Create existing hotel
        Hotel existingHotel = new Hotel();
        existingHotel.cupidId = 123L;
        existingHotel.name = "Old Name";
        existingHotel.city = "Old City";
        existingHotel.rating = BigDecimal.valueOf(3.0);
        hotelRepository.persist(existingHotel);

        List<Long> hotelIds = Collections.singletonList(123L);

        dataIngestionService.ingestHotelData(hotelIds);

        // Verify hotel was updated
        Hotel hotel = hotelRepository.findByCupidId(123L);
        assertNotNull(hotel);
        assertEquals("Mock Hotel", hotel.name); // Should be updated
        assertEquals("Mock City", hotel.city); // Should be updated
        assertEquals(BigDecimal.valueOf(4.0), hotel.rating); // Should be updated
    }

    @Test
    @Transactional
    void testIngestHotelDataWithApiFailure() {
        // Mock API failure
        Mockito.when(cupidApiClient.getPropertyById(999L))
                .thenThrow(new RuntimeException("API failure"));

        List<Long> hotelIds = List.of(123L, 999L); // One success, one failure

        // Should not throw exception, should continue processing
        assertDoesNotThrow(() -> dataIngestionService.ingestHotelData(hotelIds));

        // Verify successful hotel was still created
        Hotel hotel = hotelRepository.findByCupidId(123L);
        assertNotNull(hotel);
        assertEquals("Mock Hotel", hotel.name);

        // Verify failed hotel was not created
        Hotel failedHotel = hotelRepository.findByCupidId(999L);
        assertNull(failedHotel);
    }

    @Test
    @Transactional
    void testIngestHotelDataWithTranslationFailure() {
        // Mock translation failure
        Mockito.when(cupidApiClient.getTranslationByPropertyIdAndLanguage(123L, "fr"))
                .thenThrow(new RuntimeException("Translation API failure"));

        List<Long> hotelIds = Collections.singletonList(123L);

        // Should not throw exception
        assertDoesNotThrow(() -> dataIngestionService.ingestHotelData(hotelIds));

        // Verify hotel was still created
        Hotel hotel = hotelRepository.findByCupidId(123L);
        assertNotNull(hotel);

        // Verify only Spanish translation was created
        List<HotelTranslation> translations = hotelTranslationRepository.findByHotel(hotel);
        assertEquals(1, translations.size());
        assertEquals("es", translations.get(0).language);
    }

    @Test
    @Transactional
    void testIngestHotelDataEmptyList() {
        assertDoesNotThrow(() -> dataIngestionService.ingestHotelData(Collections.emptyList()));

        // Verify no hotels were created
        long hotelCount = hotelRepository.count();
        assertEquals(0, hotelCount);
    }
}