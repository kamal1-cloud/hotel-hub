package com.hotelhub.client;

import com.hotelhub.client.dto.CupidApiResponse;
import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import java.util.List;

@RegisterRestClient(configKey="cupid-api")
@RegisterProvider(CupidApiKeyFilter.class)
public interface CupidApiClient {

    @GET
    @Path("/v3.0/property/{id}")
    CupidPropertyDto getPropertyById(@PathParam("id") Long id);

    @GET
    @Path("/v3.0/property/reviews/{id}/10")
    List<CupidReviewDto> getReviewsByPropertyId(@PathParam("id") Long id);

    @GET
    @Path("/v3.0/property/{id}/lang/{language}")
    CupidPropertyDto getTranslationByPropertyIdAndLanguage(@PathParam("id") Long id, @PathParam("language") String language);
}
