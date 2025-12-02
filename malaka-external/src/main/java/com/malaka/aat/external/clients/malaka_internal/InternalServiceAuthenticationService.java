package com.malaka.aat.external.clients.malaka_internal;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.dto.auth.UserLoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service to handle authentication with malaka-internal service.
 * This service authenticates using a service account and caches the JWT token.
 */
@Slf4j
@Service
public class InternalServiceAuthenticationService {

    @Value("${clients.malaka-internal.base-url}")
    private String malakaInternalBaseUrl;

    @Value("${clients.malaka-internal.service-account.username}")
    private String serviceUsername;

    @Value("${clients.malaka-internal.service-account.password}")
    private String servicePassword;

    private String cachedAccessToken;
    private long tokenExpirationTime;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public InternalServiceAuthenticationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets a valid JWT access token for malaka-internal service.
     * Uses cached token if still valid, otherwise authenticates to get a new one.
     *
     * @return JWT access token
     */
    public synchronized String getAccessToken() {
        // Check if we have a cached token that's still valid
        if (cachedAccessToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            log.debug("Using cached access token for malaka-internal");
            return cachedAccessToken;
        }

        // Token expired or doesn't exist, authenticate to get a new one
        log.info("Authenticating with malaka-internal service as: {}", serviceUsername);

        try {
            // Resolve lb:// to actual URL - for RestTemplate we need to use direct URL
            // In production with Eureka, you would resolve the service URL
            String loginUrl = malakaInternalBaseUrl.replace("lb://malaka-internal", "http://localhost:8081")
                + "/api/auth/login";

            // Create login request
            UserLoginDto loginDto = new UserLoginDto();
            loginDto.setUsername(serviceUsername);
            loginDto.setPassword(servicePassword);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserLoginDto> request = new HttpEntity<>(loginDto, headers);

            // Send authentication request
            ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                loginUrl,
                request,
                BaseResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                BaseResponse baseResponse = response.getBody();

                if (baseResponse.getResultCode() == 0 && baseResponse.getData() != null) {
                    // Extract access token from response
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = objectMapper.convertValue(
                        baseResponse.getData(),
                        Map.class
                    );

                    cachedAccessToken = (String) data.get("accessToken");

                    // Cache token for 50 minutes (assuming 1 hour expiration)
                    tokenExpirationTime = System.currentTimeMillis() + (50 * 60 * 1000);

                    log.info("Successfully authenticated with malaka-internal service");
                    return cachedAccessToken;
                } else {
                    log.error("Authentication failed: resultCode={}, resultNote={}",
                        baseResponse.getResultCode(), baseResponse.getResultNote());
                    throw new RuntimeException("Failed to authenticate with malaka-internal service");
                }
            } else {
                log.error("Authentication request failed with status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to authenticate with malaka-internal service");
            }

        } catch (Exception e) {
            log.error("Error authenticating with malaka-internal service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to authenticate with malaka-internal service", e);
        }
    }

    /**
     * Invalidates the cached token, forcing re-authentication on next request.
     */
    public synchronized void invalidateToken() {
        log.info("Invalidating cached access token");
        cachedAccessToken = null;
        tokenExpirationTime = 0;
    }
}
