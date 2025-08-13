package com.hotelhub.client;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;

@Provider
public class CupidApiKeyFilter implements ClientRequestFilter {

    @Inject
    @ConfigProperty(name = "cupid.api.key")
    String apiKey;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("x-api-key", apiKey);
    }
}