package com.hotelhub.resource;

import com.hotelhub.service.HotelRetrievalService;
import com.hotelhub.service.HotelSearchService;
import com.hotelhub.service.HotelService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.math.BigDecimal;

@Path("/api/v1/hotels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Hotels", description = "Hotel management and search operations")
public class HotelResource {

    @Inject
    HotelService hotelService;

    @Inject
    HotelSearchService hotelSearchService;

    @Inject
    HotelRetrievalService hotelRetrievalService;

    @GET
    @Operation(summary = "Get all hotels", description = "Retrieve a paginated list of all hotels with optional filtering")
    @APIResponse(responseCode = "200", description = "Hotels retrieved successfully")
    public Response getAllHotels(@Parameter(description = "Page number (0-based)") @QueryParam("page") @DefaultValue("0") @Min(0) int page, @Parameter(description = "Number of items per page") @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size, @Parameter(description = "Filter by city") @QueryParam("city") String city, @Parameter(description = "Filter by country code") @QueryParam("country") String countryCode, @Parameter(description = "Minimum rating") @QueryParam("minRating") BigDecimal minRating, @Parameter(description = "Maximum rating") @QueryParam("maxRating") BigDecimal maxRating, @Parameter(description = "Minimum stars") @QueryParam("minStars") Integer minStars, @Parameter(description = "Maximum stars") @QueryParam("maxStars") Integer maxStars) {
        var hotels = hotelSearchService.getHotelsWithFilters(page, size, city, countryCode, minRating, maxRating, minStars, maxStars);
        return Response.ok(hotels).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Retrieve detailed information about a specific hotel")
    @APIResponse(responseCode = "200", description = "Hotel found")
    @APIResponse(responseCode = "404", description = "Hotel not found")
    public Response getHotelById(@Parameter(description = "Hotel ID") @PathParam("id") Long id) {
        return hotelService.getHotelById(id).map(hotelDto -> Response.ok(hotelDto).build()).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search hotels", description = "Search hotels by name, description, or location")
    @APIResponse(responseCode = "200", description = "Search results retrieved")
    public Response searchHotels(@Parameter(description = "Search query") @QueryParam("q") String query, @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") @Min(0) int page, @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        var results = hotelSearchService.searchHotels(query, page, size);
        return Response.ok(results).build();
    }

    @GET
    @Path("/{id}/reviews")
    @Operation(summary = "Get hotel reviews", description = "Retrieve reviews for a specific hotel")
    @APIResponse(responseCode = "200", description = "Reviews retrieved")
    @APIResponse(responseCode = "404", description = "Hotel not found")
    public Response getHotelReviews(@Parameter(description = "Hotel ID") @PathParam("id") Long id, @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") @Min(0) int page, @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("10") @Min(1) @Max(50) int size) {
        var reviews = hotelRetrievalService.getHotelReviews(id, page, size);
        return Response.ok(reviews).build();
    }

    @GET
    @Path("/{id}/translations")
    @Operation(summary = "Get hotel translations", description = "Retrieve translations for a specific hotel")
    @APIResponse(responseCode = "200", description = "Translations retrieved")
    @APIResponse(responseCode = "404", description = "Hotel not found")
    public Response getHotelTranslations(@Parameter(description = "Hotel ID") @PathParam("id") Long id, @Parameter(description = "Language code (fr, es)") @QueryParam("lang") String language) {
        var translations = hotelRetrievalService.getHotelTranslations(id, language);
        return Response.ok(translations).build();
    }

    @GET
    @Path("/{id}/photos")
    @Operation(summary = "Get hotel photos", description = "Retrieve photos for a specific hotel")
    @APIResponse(responseCode = "200", description = "Photos retrieved")
    @APIResponse(responseCode = "404", description = "Hotel not found")
    public Response getHotelPhotos(@Parameter(description = "Hotel ID") @PathParam("id") Long id) {
        var photos = hotelRetrievalService.getHotelPhotos(id);
        return Response.ok(photos).build();
    }

    @GET
    @Path("/{id}/facilities")
    @Operation(summary = "Get hotel facilities", description = "Retrieve facilities for a specific hotel")
    @APIResponse(responseCode = "200", description = "Facilities retrieved")
    @APIResponse(responseCode = "404", description = "Hotel not found")
    public Response getHotelFacilities(@Parameter(description = "Hotel ID") @PathParam("id") Long id) {
        var facilities = hotelRetrievalService.getHotelFacilities(id);
        return Response.ok(facilities).build();
    }

    @GET
    @Path("/{id}/rooms")
    @Operation(summary = "Get hotel rooms", description = "Retrieve rooms for a specific hotel")
    @APIResponse(responseCode = "200", description = "Rooms retrieved")
    @APIResponse(responseCode = "404", description = "Hotel not found")
    public Response getHotelRooms(@Parameter(description = "Hotel ID") @PathParam("id") Long id) {
        var rooms = hotelService.getHotelRooms(id);
        return Response.ok(rooms).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Get hotel statistics", description = "Retrieve aggregated statistics about hotels")
    @APIResponse(responseCode = "200", description = "Statistics retrieved")
    public Response getHotelStats() {
        var stats = hotelRetrievalService.getHotelStatistics();
        return Response.ok(stats).build();
    }
}