package com.malaka.aat.internal.clients;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.internal.dto.auth.UserLoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service to handle authentication with malaka-external service.
 * This service authenticates using a service account and caches the JWT token.
 */
@Slf4j
@Service
public class ExternalServiceAuthenticationService {

    @Value("${clients.malaka-external.base-url}")
    private String malakaExternalBaseUrl;

    @Value("${clients.malaka-external.service-account.username}")
    private String serviceUsername;

    @Value("${clients.malaka-external.service-account.password}")
    private String servicePassword;

    private String cachedAccessToken;
    private long tokenExpirationTime;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ExternalServiceAuthenticationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets a valid JWT access token for malaka-external service.
     * Uses cached token if still valid, otherwise authenticates to get a new one.
     *
     * @return JWT access token
     */
    public synchronized String getAccessToken() {
        // Check if we have a cached token that's still valid
        if (cachedAccessToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            log.debug("Using cached access token for malaka-external");
            return cachedAccessToken;
        }

        // Token expired or doesn't exist, authenticate to get a new one
        log.info("Authenticating with malaka-external service as: {}", serviceUsername);

        try {
            // Resolve lb:// to actual URL - for RestTemplate we need to use direct URL
            // In production with Eureka, you would resolve the service URL
            String loginUrl = malakaExternalBaseUrl.replace("lb://malaka-external", "http://localhost:8082")
                + "/api/external/auth/login";

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

                    log.info("Successfully authenticated with malaka-external service");
                    return cachedAccessToken;
                } else {
                    log.error("Authentication failed: resultCode={}, resultNote={}",
                        baseResponse.getResultCode(), baseResponse.getResultNote());
                    throw new RuntimeException("Failed to authenticate with malaka-external service");
                }
            } else {
                log.error("Authentication request failed with status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to authenticate with malaka-external service");
            }

        } catch (Exception e) {
            log.error("Error authenticating with malaka-external service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to authenticate with malaka-external service", e);
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
