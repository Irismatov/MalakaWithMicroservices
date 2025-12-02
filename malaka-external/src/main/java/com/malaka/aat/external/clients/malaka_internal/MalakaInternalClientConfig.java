package com.malaka.aat.external.clients.malaka_internal;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class MalakaInternalClientConfig {

    private final InternalServiceAuthenticationService authService;

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
