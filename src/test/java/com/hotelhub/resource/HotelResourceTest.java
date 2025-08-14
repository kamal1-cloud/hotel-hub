package com.hotelhub.resource;

import com.hotelhub.entity.Hotel;
import com.hotelhub.service.HotelCoreService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
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
@TestProfile(TestHotelProfile.class)
public class HotelResourceTest {
    @Inject
    HotelCoreService hotelCoreService;

    @BeforeEach
    @Transactional
    public void setup() {
        hotelCoreService.deleteAllHotels();

        // Create test data
        Hotel hotel1 = new Hotel();
        hotel1.cupidId = 12345L;
        hotel1.name = "Test Hotel 1";
        hotel1.address = "123 Test St";
        hotel1.city = "Test City";
        hotel1.countryCode = "US";
        hotel1.rating = BigDecimal.valueOf(4.5);
        hotel1.stars = 4;
        hotelCoreService.createHotel(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.cupidId = 67890L;
        hotel2.name = "Another Hotel";
        hotel2.address = "456 Another St";
        hotel2.city = "Another City";
        hotel2.countryCode = "CA";
        hotel2.rating = BigDecimal.valueOf(3.8);
        hotel2.stars = 3;
        hotelCoreService.createHotel(hotel2);
    }

    @Test
    public void testGetAllHotels() {
        given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("content[0].name", anyOf(is("Test Hotel 1"), is("Another Hotel")));
    }

    @Test
    public void testGetAllHotelsWithPagination() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 1)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("page", is(0))
                .body("size", is(1))
                .body("totalElements", is(2));
    }

    @Test
    public void testGetAllHotelsWithCityFilter() {
        given()
                .queryParam("city", "Test City")
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", is("Test Hotel 1"));
    }

    @Test
    public void testGetAllHotelsWithRatingFilter() {
        given()
                .queryParam("minRating", BigDecimal.valueOf(4.0))
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", is("Test Hotel 1"));
    }

    @Test
    public void testGetHotelById() {
        // Get the first hotel ID
        Long hotelId = given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .when().get("/api/v1/hotels/{id}", hotelId)
                .then()
                .statusCode(200)
                .body("id", is(hotelId.intValue()))
                .body("name", anyOf(is("Test Hotel 1"), is("Another Hotel")));
    }

    @Test
    public void testGetHotelByIdNotFound() {
        given()
                .when().get("/api/v1/hotels/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testSearchHotels() {
        given()
                .queryParam("q", "Test")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", is("Test Hotel 1"));
    }

    @Test
    public void testSearchHotelsEmpty() {
        given()
                .queryParam("q", "NonExistent")
                .when().get("/api/v1/hotels/search")
                .then()
                .statusCode(200)
                .body("content", hasSize(0));
    }

    @Test
    public void testGetHotelReviews() {
        Long hotelId = given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .when().get("/api/v1/hotels/{id}/reviews", hotelId)
                .then()
                .statusCode(200)
                .body("content", hasSize(0)); // No reviews in test data
    }

    @Test
    public void testGetHotelPhotos() {
        Long hotelId = given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .when().get("/api/v1/hotels/{id}/photos", hotelId)
                .then()
                .statusCode(200)
                .body("size()", is(0)); // No photos in test data
    }

    @Test
    public void testGetHotelFacilities() {
        Long hotelId = given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .when().get("/api/v1/hotels/{id}/facilities", hotelId)
                .then()
                .statusCode(200)
                .body("size()", is(0)); // No facilities in test data
    }

    @Test
    public void testGetHotelTranslations() {
        Long hotelId = given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .when().get("/api/v1/hotels/{id}/translations", hotelId)
                .then()
                .statusCode(200)
                .body("size()", is(0)); // No translations in test data
    }

    @Test
    public void testGetHotelTranslationsWithLanguage() {
        Long hotelId = given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getLong("content[0].id");

        given()
                .queryParam("lang", "fr")
                .when().get("/api/v1/hotels/{id}/translations", hotelId)
                .then()
                .statusCode(200)
                .body("size()", is(0)); // No translations in test data
    }

    @Test
    public void testGetHotelStats() {
        given()
                .when().get("/api/v1/hotels/stats")
                .then()
                .statusCode(200)
                .body("totalHotels", is(2))
                .body("averageRating", notNullValue())
                .body("hotelsByCountry", notNullValue());
    }
}