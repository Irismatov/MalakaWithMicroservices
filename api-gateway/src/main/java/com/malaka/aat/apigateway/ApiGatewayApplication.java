package com.malaka.aat.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * API Gateway Application using Spring Cloud Gateway.
 * Acts as a single entry point for all microservices in the system.
 *
 * Key Features:
 * - Dynamic routing based on Eureka service discovery
 * - Load balancing across service instances
 * - Circuit breaker integration with Resilience4j
 * - Request/response logging
 * - CORS configuration
 * - Rate limiting support
 *
 * Best Practices Implemented:
 * - Reactive programming model (WebFlux)
 * - Service discovery integration
 * - Health checks via Actuator
 * - Centralized routing configuration
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
