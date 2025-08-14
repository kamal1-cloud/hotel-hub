package com.hotelhub.service;

import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelFacility;
import com.hotelhub.entity.HotelPhoto;
import com.hotelhub.entity.HotelReview;
import com.hotelhub.entity.HotelTranslation;
import com.hotelhub.repository.HotelFacilityRepository;
import com.hotelhub.repository.HotelPhotoRepository;
import com.hotelhub.repository.HotelRepository;
import com.hotelhub.repository.HotelReviewRepository;
import com.hotelhub.repository.HotelTranslationRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(TestServiceProfile.class)
public class HotelServiceTest {

    @Inject
    HotelService hotelService;

    @Inject
    HotelSearchService hotelSearchService;

    @Inject
    HotelRetrievalService hotelRetrievalService;

    @Inject
    HotelRepository hotelRepository;

    @Inject
    HotelReviewRepository hotelReviewRepository;

    @Inject
    HotelPhotoRepository hotelPhotoRepository;

    @Inject
    HotelFacilityRepository hotelFacilityRepository;

    @Inject
    HotelTranslationRepository hotelTranslationRepository;

    private Hotel testHotel;

    @BeforeEach
    @Transactional
    public void setup() {
        // Clean up
        hotelReviewRepository.deleteAll();
        hotelPhotoRepository.deleteAll();
        hotelFacilityRepository.deleteAll();
        hotelTranslationRepository.deleteAll();
        hotelRepository.deleteAll();

        // Create test hotel
        testHotel = new Hotel();
        testHotel.cupidId = 12345L;
        testHotel.name = "Test Hotel";
        testHotel.address = "123 Test St";
        testHotel.city = "Test City";
        testHotel.countryCode = "US";
        testHotel.rating = BigDecimal.valueOf(4.5);
        testHotel.stars = 4;
        hotelRepository.persist(testHotel);

        // Create test review
        HotelReview review = new HotelReview();
        review.hotel = testHotel;
        review.cupidReviewId = "review123";
        review.averageScore = BigDecimal.valueOf(4.5);
        review.headline = "Great stay!";
        review.country = "US";
        hotelReviewRepository.persist(review);

        // Create test photo
        HotelPhoto photo = new HotelPhoto();
        photo.hotel = testHotel;
        photo.url = "http://test.com/photo.jpg";
        photo.mainPhoto = true;
        photo.score = BigDecimal.valueOf(8.5);
        hotelPhotoRepository.persist(photo);

        // Create test facility
        HotelFacility facility = new HotelFacility();
        facility.hotel = testHotel;
        facility.facilityId = 1;
        facility.name = "WiFi";
        hotelFacilityRepository.persist(facility);

        // Create test translation
        HotelTranslation translation = new HotelTranslation();
        translation.hotel = testHotel;
        translation.language = "fr";
        translation.translatedName = "Hôtel Test";
        translation.translatedDescription = "Description en français";
        translation.translatedAddress = "123 Rue Test";
        hotelTranslationRepository.persist(translation);
    }

    @Test
    public void testGetHotelById() {
        var result = hotelService.getHotelById(testHotel.id);
        assertTrue(result.isPresent());
        assertEquals("Test Hotel", result.get().name);
        assertEquals("Test City", result.get().city);
    }

    @Test
    public void testGetHotelByIdNotFound() {
        var result = hotelService.getHotelById(99999L);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetHotelsWithFilters() {
        var result = hotelSearchService.getHotelsWithFilters(0, 10, "Test City", null, null, null, null, null);
        assertEquals(1, result.content.size());
        assertEquals("Test Hotel", result.content.get(0).name);
    }

    @Test
    public void testGetHotelsWithRatingFilter() {
        var result = hotelSearchService.getHotelsWithFilters(0, 10, null, null, BigDecimal.valueOf(4.0), null, null, null);
        assertEquals(1, result.content.size());

        var resultEmpty = hotelSearchService.getHotelsWithFilters(0, 10, null, null, BigDecimal.valueOf(5.0), null, null, null);
        assertEquals(0, resultEmpty.content.size());
    }

    @Test
    public void testSearchHotels() {
        var result = hotelSearchService.searchHotels("Test", 0, 10);
        assertEquals(1, result.content.size());
        assertEquals("Test Hotel", result.content.get(0).name);
    }

    @Test
    public void testSearchHotelsEmpty() {
        var result = hotelSearchService.searchHotels("NonExistent", 0, 10);
        assertEquals(0, result.content.size());
    }

    @Test
    public void testGetHotelReviews() {
        var result = hotelRetrievalService.getHotelReviews(testHotel.id, 0, 10);
        assertEquals(1, result.content.size());
        assertEquals("Great stay!", result.content.get(0).headline);
        assertEquals(4.5, result.content.get(0).averageScore.doubleValue(), 0.001);
    }

    @Test
    public void testGetHotelReviewsNotFound() {
        var result = hotelRetrievalService.getHotelReviews(99999L, 0, 10);
        assertEquals(0, result.content.size());
    }

    @Test
    public void testGetHotelPhotos() {
        var result = hotelRetrievalService.getHotelPhotos(testHotel.id);
        assertEquals(1, result.size());
        assertEquals("http://test.com/photo.jpg", result.get(0).url);
        assertTrue(result.get(0).mainPhoto);
    }

    @Test
    public void testGetHotelPhotosNotFound() {
        var result = hotelRetrievalService.getHotelPhotos(99999L);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetHotelFacilities() {
        var result = hotelRetrievalService.getHotelFacilities(testHotel.id);
        assertEquals(1, result.size());
        assertEquals("WiFi", result.get(0).name);
        assertEquals(Integer.valueOf(1), result.get(0).facilityId);
    }

    @Test
    public void testGetHotelFacilitiesNotFound() {
        var result = hotelRetrievalService.getHotelFacilities(99999L);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetHotelTranslations() {
        var result = hotelRetrievalService.getHotelTranslations(testHotel.id, null);
        assertEquals(1, result.size());
        assertEquals("fr", result.get(0).language);
        assertEquals("Hôtel Test", result.get(0).translatedName);
    }

    @Test
    public void testGetHotelTranslationsWithLanguage() {
        var result = hotelRetrievalService.getHotelTranslations(testHotel.id, "fr");
        assertEquals(1, result.size());
        assertEquals("fr", result.get(0).language);

        var resultEmpty = hotelRetrievalService.getHotelTranslations(testHotel.id, "de");
        assertEquals(0, resultEmpty.size());
    }

    @Test
    public void testGetHotelTranslationsNotFound() {
        var result = hotelRetrievalService.getHotelTranslations(99999L, null);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetHotelStatistics() {
        var stats = hotelRetrievalService.getHotelStatistics();
        assertEquals(1L, stats.totalHotels);
        assertEquals(0, BigDecimal.valueOf(4.5).compareTo(BigDecimal.valueOf(stats.averageRating)));
        assertEquals(Integer.valueOf(4), stats.maxStars);
        assertEquals(Integer.valueOf(4), stats.minStars);
        assertEquals(1L, stats.totalReviews);
        assertTrue(stats.hotelsByCountry.containsKey("US"));
        assertEquals(1L, stats.hotelsByCountry.get("US"));
    }
}