package com.hotelhub.resource;

import com.hotelhub.service.DataIngestionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/v1/ingest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Data Ingestion", description = "Operations for ingesting data from external APIs")
public class IngestionResource {

    @Inject
    DataIngestionService dataIngestionService;

    @POST
    @Operation(
            summary = "Ingest hotel data",
            description = "Ingest hotel data from Cupid API for the provided hotel IDs. This will fetch property details, reviews, and translations."
    )
    @APIResponse(
            responseCode = "200",
            description = "Data ingestion initiated successfully",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @APIResponse(
            responseCode = "500",
            description = "Data ingestion failed",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    public Response ingestData(
            @Schema(description = "List of hotel IDs to ingest", example = "[1641879, 317597, 1202743]")
            List<Long> hotelIds) {
        try {
            dataIngestionService.ingestHotelData(hotelIds);
            return Response.ok("Data ingestion initiated successfully.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Data ingestion failed: " + e.getMessage()).build();
        }
    }
}
