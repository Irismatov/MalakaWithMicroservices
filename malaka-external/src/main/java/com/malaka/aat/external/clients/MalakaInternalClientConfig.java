package com.malaka.aat.external.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign Client to communicate with malaka-internal service.
 * This configuration uses service account authentication for inter-service communication.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MalakaInternalClientConfig {

    private final InternalServiceAuthenticationService authService;

    /**
     * Request interceptor that adds service account JWT token
     * to all outgoing Feign client requests to malaka-internal.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                try {
                    // Get service account access token
                    String accessToken = authService.getAccessToken();

                    if (accessToken != null && !accessToken.isEmpty()) {
                        log.debug("Adding service account Authorization header to Feign request");
                        template.header("Authorization", "Bearer " + accessToken);
                    } else {
                        log.warn("No access token available for malaka-internal request");
                    }

                } catch (Exception e) {
                    log.error("Error getting access token for malaka-internal: {}", e.getMessage());
                    // Don't fail the request, let it proceed without token and let the server reject it
                }
            }
        };
    }

    /**
     * Error decoder to handle 401/403 responses by invalidating cached token
     */
    @Bean
    public feign.codec.ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 401 || response.status() == 403) {
                log.warn("Received {} from malaka-internal, invalidating cached token", response.status());
                authService.invalidateToken();

                // Return RetryableException to trigger retry with new token
                // Constructor: RetryableException(int status, String message, HttpMethod httpMethod, Long retryAfter, Request request)
                return new RetryableException(
                    response.status(),
                    "Unauthorized - token may have expired",
                    response.request().httpMethod(),
                    (Long) null,  // retryAfter - null means retry immediately
                    response.request()
                );
            }

            // For other errors, use default decoder
            return new feign.codec.ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}
