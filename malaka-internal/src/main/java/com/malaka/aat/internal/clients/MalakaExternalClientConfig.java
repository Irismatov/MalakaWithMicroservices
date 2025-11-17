package com.malaka.aat.internal.clients;

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
public class MalakaExternalClientConfig {

    private final ExternalServiceAuthenticationService authService;

    @Bean
    public RequestInterceptor externalServiceRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                try {
                    // Get service account access token
                    String accessToken = authService.getAccessToken();

                    if (accessToken != null && !accessToken.isEmpty()) {
                        log.debug("Adding service account Authorization header to Feign request for malaka-external");
                        template.header("Authorization", "Bearer " + accessToken);
                        template.header("X-Internal-Request", "account-service");
                    } else {
                        log.warn("No access token available for malaka-external request");
                    }

                } catch (Exception e) {
                    log.error("Error getting access token for malaka-external: {}", e.getMessage());
                    // Don't fail the request, let it proceed without token and let the server reject it
                }
            }
        };
    }


    @Bean
    public feign.codec.ErrorDecoder externalServiceErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 401 || response.status() == 403) {
                log.warn("Received {} from malaka-external, invalidating cached token", response.status());
                authService.invalidateToken();

                // Return RetryableException to trigger retry with new token
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
