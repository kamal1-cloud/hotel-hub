package com.hotelhub.resource;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.HashMap;
import java.util.Map;

public class TestHotelProfile implements QuarkusTestProfile {
    
    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new HashMap<>();
        // Isolated test containers - DevServices will manage database connection
        config.put("quarkus.datasource.devservices.enabled", "true");
        config.put("quarkus.datasource.devservices.reuse", "false");
        config.put("quarkus.datasource.devservices.image-name", "postgres:15-alpine");
        // Disable cache for clean test state
        config.put("quarkus.cache.enabled", "false");
        // Mock API for tests
        config.put("quarkus.rest-client.cupid-api.url", "http://localhost:8081");
        return config;
    }
}