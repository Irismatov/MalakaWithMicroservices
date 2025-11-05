package com.malaka.aat.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Gateway Route Configuration.
 * Defines custom routes and routing rules for the API Gateway.
 *
 * Best Practices:
 * - Use predicates for route matching
 * - Apply filters for request/response modification
 * - Implement circuit breakers for resilience
 * - Use load balancing with 'lb://' scheme
 *
 * Route Pattern:
 * - /api/service-name/** -> lb://SERVICE-NAME/**
 */
@Configuration
public class GatewayConfig {

    /**
     * Custom route definitions using RouteLocatorBuilder.
     * These routes complement the automatic discovery-based routing.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Example: Eureka Server Dashboard route
                .route("eureka-dashboard", r -> r
                        .path("/eureka/web")
                        .filters(f -> f
                                .setPath("/"))
                        .uri("lb://service-registry"))

                // Example: Actuator endpoints (for future services)
                .route("actuator-route", r -> r
                        .path("/actuator/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("actuatorCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")))
                        .uri("lb://service-registry"))

                // Example: API versioning route
                .route("api-v1-route", r -> r
                        .path("/api/v1/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/v1
                                .addRequestHeader("X-API-Version", "v1")
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://your-service-name")) // Replace with actual service

                // Fallback route for undefined paths
                .route("fallback-route", r -> r
                        .path("/fallback")
                        .filters(f -> f
                                .setStatus(503))
                        .uri("forward:/fallback"))

                .build();
    }

    // CORS is configured in application.yml using Spring Cloud Gateway's reactive CORS support
    // No need for servlet-based CorsConfigurationSource in reactive gateway
}
