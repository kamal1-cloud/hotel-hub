package com.hotelhub;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "Hotel Hub API",
                version = "1.0.0",
                description = "A comprehensive hotel management API that consumes data from the Cupid API and provides enhanced search, filtering, and aggregation capabilities.",
                contact = @Contact(
                        name = "Hotel Hub Support",
                        email = "support@hotelhub.com"
                ),
                license = @License(
                        name = "MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development server"),
                @Server(url = "https://api.hotelhub.com", description = "Production server")
        },
        tags = {
                @Tag(name = "Hotels", description = "Hotel management and search operations"),
                @Tag(name = "Data Ingestion", description = "Operations for ingesting data from external APIs")
        }
)
public class HotelHubApplication extends Application {
}