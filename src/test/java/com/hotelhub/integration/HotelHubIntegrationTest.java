package com.hotelhub.integration;

import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelReview;
import com.hotelhub.entity.HotelTranslation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestProfile(TestIntegrationProfile.class)
public class HotelHubIntegrationTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setupTestData() {
        // Clean up in FK-safe order
        em.createQuery("DELETE FROM HotelReview").executeUpdate();
        em.createQuery("DELETE FROM HotelTranslation").executeUpdate();
        em.createQuery("DELETE FROM Hotel").executeUpdate();

        // Create comprehensive test data
        Hotel hotel1 = createTestHotel(1L, "Luxury Resort", "New York", "US", 4.8, 5);
        Hotel hotel2 = createTestHotel(2L, "Budget Inn", "Toronto", "CA", 3.2, 2);
        Hotel hotel3 = createTestHotel(3L, "Business Hotel", "London", "GB", 4.1, 4);

        // Reviews
        createTestReview(hotel1, "review1", 4.5, "Excellent service", "Great location", "Small room");
        createTestReview(hotel1, "review2", 5.0, "Perfect stay", "Amazing view", "Expensive");
        createTestReview(hotel2, "review3", 3.0, "Decent value", "Good price", "Old facilities");

        // Translations
        createTestTranslation(hotel1, "fr", "Resort de Luxe", "Description en français", "Adresse en français");
        createTestTranslation(hotel1, "es", "Resort de Lujo", "Descripción en español", "Dirección en español");
        createTestTranslation(hotel2, "fr", "Auberge Budget", "Description budget", "Adresse budget");

        em.flush();
        em.clear();
    }

    private Hotel createTestHotel(Long cupidId, String name, String city, String country, Double rating, Integer stars) {
        Hotel hotel = new Hotel();
        hotel.cupidId = cupidId;
        hotel.name = name;
        hotel.address = "123 " + name + " Street";
        hotel.city = city;
        hotel.countryCode = country;
        hotel.rating = BigDecimal.valueOf(rating);
        hotel.stars = stars;
        hotel.description = "Description for " + name;
        hotel.phone = "+1-555-000-000" + cupidId;
        hotel.email = name.toLowerCase().replace(" ", "") + "@test.com";
        em.persist(hotel);
        return hotel;
    }

    private void createTestReview(Hotel hotel, String reviewId, Double score, String headline, String pros, String cons) {
        HotelReview review = new HotelReview();
        review.hotel = hotel;
        review.cupidReviewId = reviewId;
        review.averageScore = BigDecimal.valueOf(score);
        review.headline = headline;
        review.pros = pros;
        review.cons = cons;
        review.country = hotel.countryCode;
        review.type = "leisure";
        review.name = "Test Reviewer";
        review.date = "2024-01-15";
        review.language = "en";
        review.source = "test";
        em.persist(review);
    }

    private void createTestTranslation(Hotel hotel, String language, String name, String description, String address) {
        HotelTranslation translation = new HotelTranslation();
        translation.hotel = hotel;
        translation.language = language;
        translation.translatedName = name;
        translation.translatedDescription = description;
        translation.translatedAddress = address;
        em.persist(translation);
    }

    @Test
    public void testCompleteHotelWorkflow() {
        // 1. Get all hotels
        given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(3))
                .body("totalElements", is(3));

        // 2. Filter by country
        given()
                .queryParam("country", "US")
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", is("Luxury Resort"));

        // 3. Filter by rating
        given()
                .queryParam("minRating", 4.0)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(2));

        // 4. Search
        given()
                .queryParam("q", "luxury")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", is("Luxury Resort"));

        // 5. Get specific hotel
        Long luxuryHotelId = given()
                .queryParam("q", "luxury")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .when().get("/api/v1/hotels/{id}", luxuryHotelId)
                .then()
                .statusCode(200)
                .body("name", is("Luxury Resort"))
                .body("city", is("New York"))
                .body("rating", is(4.8F));

        // 6. Reviews
        given()
                .when().get("/api/v1/hotels/{id}/reviews", luxuryHotelId)
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("content[0].headline", anyOf(is("Excellent service"), is("Perfect stay")));

        // 7. Translations
        given()
                .when().get("/api/v1/hotels/{id}/translations", luxuryHotelId)
                .then()
                .statusCode(200)
                .body("size()", is(2));

        given()
                .queryParam("lang", "fr")
                .when().get("/api/v1/hotels/{id}/translations", luxuryHotelId)
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].translatedName", is("Resort de Luxe"));

        // 8. Stats
        given()
                .when().get("/api/v1/hotels/stats")
                .then()
                .statusCode(200)
                .body("totalHotels", is(3))
                .body("averageRating", notNullValue())
                .body("totalReviews", is(3))
                .body("hotelsByCountry.US", is(1))
                .body("hotelsByCountry.CA", is(1))
                .body("hotelsByCountry.GB", is(1));
    }

    @Test
    public void testPaginationAndSorting() {
        given()
                .queryParam("page", 0).queryParam("size", 2)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("page", is(0))
                .body("size", is(2))
                .body("totalElements", is(3))
                .body("totalPages", is(2))
                .body("first", is(true))
                .body("last", is(false));

        given()
                .queryParam("page", 1).queryParam("size", 2)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("page", is(1))
                .body("first", is(false))
                .body("last", is(true));
    }

    @Test
    public void testAdvancedFiltering() {
        given()
                .queryParam("minRating", 3.0)
                .queryParam("maxRating", 4.5)
                .queryParam("minStars", 2)
                .queryParam("maxStars", 4)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(2));

        given()
                .queryParam("city", "toronto")
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", is("Budget Inn"));
    }

    @Test
    public void testErrorHandling() {
        given()
                .when().get("/api/v1/hotels/99999")
                .then()
                .statusCode(404);

        given()
                .queryParam("page", -1)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(400);

        given()
                .queryParam("size", 0)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(400);

        given()
                .queryParam("size", 101)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(400);
    }

    @Test
    public void testSearchEdgeCases() {
        given()
                .queryParam("q", "")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .body("content", hasSize(3));

        given()
                .queryParam("q", "nonexistent")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .body("content", hasSize(0));

        given()
                .queryParam("q", "LUXURY")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .body("content", hasSize(1));
    }
}