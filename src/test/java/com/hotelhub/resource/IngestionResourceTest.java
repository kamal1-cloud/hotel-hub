package com.hotelhub.resource;

import com.hotelhub.client.CupidApiClient;
import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.service.HotelCoreService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
@TestProfile(TestIngestionProfile.class)
public class IngestionResourceTest {

    @InjectMock
    @org.eclipse.microprofile.rest.client.inject.RestClient
    CupidApiClient cupidApiClient;

    @Inject
    HotelCoreService hotelCoreService;

    @BeforeEach
    @Transactional
    void setup() {
        hotelCoreService.deleteAllHotels();

        // Mock property data
        CupidPropertyDto propertyDto = new CupidPropertyDto();
        propertyDto.hotelId = 123L;
        propertyDto.hotelName = "Mock Hotel";
        propertyDto.rating = BigDecimal.valueOf(4.0);
        propertyDto.stars = 4;
        propertyDto.address = new CupidPropertyDto.Address();
        propertyDto.address.address = "Mock Address";
        propertyDto.address.city = "Mock City";
        propertyDto.address.country = "US";

        // Mock review data
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
        frProperty.description = "Description en français";
        frProperty.address = new CupidPropertyDto.Address();
        frProperty.address.address = "Adresse Maquette";

        CupidPropertyDto esProperty = new CupidPropertyDto();
        esProperty.hotelName = "Hotel Maqueta";
        esProperty.description = "Descripción en español";
        esProperty.address = new CupidPropertyDto.Address();
        esProperty.address.address = "Dirección Maqueta";

        // Setup mocks
        Mockito.when(cupidApiClient.getPropertyById(anyLong())).thenReturn(propertyDto);
        Mockito.when(cupidApiClient.getReviewsByPropertyId(anyLong())).thenReturn(reviewsList);
        Mockito.when(cupidApiClient.getTranslationByPropertyIdAndLanguage(anyLong(), eq("fr"))).thenReturn(frProperty);
        Mockito.when(cupidApiClient.getTranslationByPropertyIdAndLanguage(anyLong(), eq("es"))).thenReturn(esProperty);
    }

    @Test
    void testIngestDataSuccess() {
        given()
                .contentType(ContentType.JSON)
                .body(Collections.singletonList(123L))
                .when().post("/api/v1/ingest")
                .then()
                .statusCode(200);

        // Verify hotel was created
        given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200);
    }

    @Test
    void testIngestDataEmptyList() {
        given()
                .contentType(ContentType.JSON)
                .body(Collections.emptyList())
                .when().post("/api/v1/ingest")
                .then()
                .statusCode(200);
    }

    @Test
    void testIngestDataWithApiFailure() {
        // Mock API failure
        Mockito.when(cupidApiClient.getPropertyById(999L))
                .thenThrow(new RuntimeException("API failure"));

        given()
                .contentType(ContentType.JSON)
                .body(Collections.singletonList(999L))
                .when().post("/api/v1/ingest")
                .then()
                .statusCode(200); // Should continue processing even with failures
    }

    @Test
    void testIngestDataInvalidInput() {
        given()
                .contentType(ContentType.JSON)
                .body("invalid json")
                .when().post("/api/v1/ingest")
                .then()
                .statusCode(400);
    }
}