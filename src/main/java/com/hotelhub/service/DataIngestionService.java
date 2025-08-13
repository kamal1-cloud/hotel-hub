package com.hotelhub.service;

import com.hotelhub.client.CupidApiClient;
import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.entity.*;
import com.hotelhub.service.HotelService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class DataIngestionService {

    private static final Logger LOG = Logger.getLogger(DataIngestionService.class);

    @Inject
    @RestClient
    CupidApiClient cupidApiClient;

    @Inject
    HotelService hotelService;

    @ConfigProperty(name = "cupid.api.max-retries", defaultValue = "5")
    int maxRetries;

    @ConfigProperty(name = "cupid.api.retry-delay", defaultValue = "2000")
    long retryDelay;

    @ConfigProperty(name = "cupid.api.requests-per-minute", defaultValue = "60")
    int requestsPerMinute;

    @ConfigProperty(name = "cupid.api.batch-delay", defaultValue = "1000")
    long batchDelay;

    @ConfigProperty(name = "cupid.api.circuit-breaker-failure-threshold", defaultValue = "10")
    int circuitBreakerThreshold;

    @ConfigProperty(name = "cupid.api.circuit-breaker-timeout", defaultValue = "300000")
    long circuitBreakerTimeout;

    // Rate limiting state
    private final AtomicLong lastRequestTime = new AtomicLong(0);
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());

    // Circuit breaker state
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private final AtomicLong circuitOpenTime = new AtomicLong(0);

    public void ingestHotelData(List<Long> hotelIds) {

        LOG.info("Starting ingestion of " + hotelIds.size() + " hotel IDs with rate limiting");
        int processed = 0;
        int successful = 0;
        int failed = 0;

        for (Long hotelId : hotelIds) {
            processed++;
            
            try {
                // Apply rate limiting
                applyRateLimit();
                
                // Check circuit breaker
                if (isCircuitOpen()) {
                    LOG.warn("Circuit breaker is open, skipping hotel ID: " + hotelId);
                    failed++;
                    continue;
                }

                LOG.info("Processing hotel " + processed + "/" + hotelIds.size() + ": ID " + hotelId);
                
                // Process individual hotel in separate transaction
                ingestSingleHotel(hotelId);
                successful++;
                consecutiveFailures.set(0); // Reset failure count on success

            } catch (Exception e) {
                failed++;
                consecutiveFailures.incrementAndGet();
                
                if (isRateLimitError(e)) {
                    LOG.warn("Rate limit exceeded for hotel ID: " + hotelId + ". Backing off...", e);
                    try {
                        Thread.sleep(60000); // Wait 1 minute for rate limit reset
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else if (isTransientError(e)) {
                    LOG.warn("Transient error for hotel ID: " + hotelId + ". Will continue with others: " + e.getMessage());
                } else {
                    LOG.error("Permanent error ingesting data for hotel ID: " + hotelId, e);
                }
                
                // Check if we should open the circuit breaker
                if (consecutiveFailures.get() >= circuitBreakerThreshold) {
                    LOG.warn("Opening circuit breaker due to " + consecutiveFailures.get() + " consecutive failures");
                    circuitOpenTime.set(System.currentTimeMillis());
                }

                // Add small delay between failures to prevent overwhelming the API
                try {
                    Thread.sleep(batchDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Add progress logging every 10 hotels
            if (processed % 10 == 0) {
                LOG.info("Progress: " + processed + "/" + hotelIds.size() + " processed (Success: " + successful + ", Failed: " + failed + ")");
            }
        }

        LOG.info("Ingestion completed. Total: " + processed + ", Successful: " + successful + ", Failed: " + failed);
    }

    private <T> T executeWithRetry(java.util.function.Supplier<T> operation, String operationName) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                LOG.debug("Attempting " + operationName + " (attempt " + attempt + "/" + maxRetries + ")");
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                LOG.warn("Attempt " + attempt + " failed for " + operationName + ": " + e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting to retry", ie);
                    }
                }
            }
        }
        throw new RuntimeException("Failed after " + maxRetries + " attempts: " + operationName, lastException);
    }

    private void applyRateLimit() {
        long currentTime = System.currentTimeMillis();
        long windowDuration = 60000; // 1 minute in milliseconds

        // Reset window if it's been more than a minute
        long windowStartTime = windowStart.get();
        if (currentTime - windowStartTime >= windowDuration) {
            windowStart.set(currentTime);
            requestCount.set(0);
        }

        // Check if we've exceeded the rate limit
        int currentRequests = requestCount.get();
        if (currentRequests >= requestsPerMinute) {
            long waitTime = windowDuration - (currentTime - windowStartTime);
            if (waitTime > 0) {
                LOG.info("Rate limit reached (" + requestsPerMinute + " requests/minute). Waiting " + waitTime + " ms");
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for rate limit reset", e);
                }
                // Reset the window after waiting
                windowStart.set(System.currentTimeMillis());
                requestCount.set(0);
            }
        }

        // Increment request count and add delay between requests
        requestCount.incrementAndGet();
        
        // Add minimum delay between requests to be gentle on the API
        long timeSinceLastRequest = currentTime - lastRequestTime.get();
        long minDelay = 1000; // 1 second minimum between requests
        if (timeSinceLastRequest < minDelay) {
            try {
                Thread.sleep(minDelay - timeSinceLastRequest);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while applying rate limit delay", e);
            }
        }
        
        lastRequestTime.set(System.currentTimeMillis());
    }

    private boolean isCircuitOpen() {
        long currentTime = System.currentTimeMillis();
        long openTime = circuitOpenTime.get();
        
        if (openTime > 0) {
            // Circuit is open, check if timeout has passed
            if (currentTime - openTime > circuitBreakerTimeout) {
                LOG.info("Circuit breaker timeout expired, closing circuit");
                circuitOpenTime.set(0);
                consecutiveFailures.set(0);
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isRateLimitError(Exception e) {
        String message = e.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            return lowerMessage.contains("rate limit") || 
                   lowerMessage.contains("429") || 
                   lowerMessage.contains("too many requests") ||
                   lowerMessage.contains("quota exceeded");
        }
        return false;
    }

    private boolean isTransientError(Exception e) {
        String message = e.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            return lowerMessage.contains("timeout") ||
                   lowerMessage.contains("connection") ||
                   lowerMessage.contains("502") ||
                   lowerMessage.contains("503") ||
                   lowerMessage.contains("504") ||
                   lowerMessage.contains("temporary") ||
                   lowerMessage.contains("unavailable") ||
                   lowerMessage.contains("unable to acquire jdbc connection") ||
                   lowerMessage.contains("the transaction has rolled back") ||
                   lowerMessage.contains("database connection") ||
                   lowerMessage.contains("connection pool");
        }
        return false;
    }

    @Transactional
    public void ingestSingleHotel(Long hotelId) throws Exception {
        // Fetch property details with retry
        CupidPropertyDto property = executeWithRetry(() -> {
            var response = cupidApiClient.getPropertyById(hotelId);
            if (response == null) {
                throw new RuntimeException("Empty response from Cupid API for hotel ID: " + hotelId);
            }
            return response;
        }, "property data for hotel " + hotelId);

        // Use service layer for hotel management
        Hotel hotel = hotelService.findByCupidId(hotelId);
        if (hotel == null) {
            hotel = hotelService.createFromCupidData(property);
        } else {
            hotel = hotelService.updateFromCupidData(hotel, property);
        }

        // Use service layer for related data updates
        // Fetch and update reviews using service layer
        var reviews = executeWithRetry(() -> cupidApiClient.getReviewsByPropertyId(hotelId),
            "reviews for hotel " + hotelId);

        // Fetch translations using service layer
        CupidPropertyDto frenchData = null;
        CupidPropertyDto spanishData = null;

        try {
            frenchData = executeWithRetry(() ->
                cupidApiClient.getTranslationByPropertyIdAndLanguage(hotelId, "fr"),
                "French translation for hotel " + hotelId);
        } catch (Exception e) {
            LOG.warn("Failed to fetch French translation for hotel " + hotelId, e);
        }

        try {
            spanishData = executeWithRetry(() ->
                cupidApiClient.getTranslationByPropertyIdAndLanguage(hotelId, "es"),
                "Spanish translation for hotel " + hotelId);
        } catch (Exception e) {
            LOG.warn("Failed to fetch Spanish translation for hotel " + hotelId, e);
        }

        hotelService.updateHotelCompleteData(hotel, property, reviews, frenchData, spanishData);

        LOG.info("Successfully ingested data for hotel ID: " + hotelId);
    }
}