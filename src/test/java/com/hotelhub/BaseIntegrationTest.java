package com.hotelhub;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for integration tests that provides consistent test container configuration.
 * Each test class that extends this will get its own isolated database container.
 */
@QuarkusTest
public abstract class BaseIntegrationTest {

    @BeforeEach
    void setupBaseTest() {
        // Common setup for all integration tests
        // Each test class will have its own isolated database
        System.setProperty("quarkus.datasource.devservices.reuse", "false");
        System.setProperty("quarkus.cache.enabled", "false");
    }
}