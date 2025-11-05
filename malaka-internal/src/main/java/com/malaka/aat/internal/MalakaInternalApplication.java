package com.malaka.aat.internal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Malaka Internal Service Application.
 *
 * This microservice provides internal business logic and APIs.
 * It is registered with Eureka Service Registry and accessible via API Gateway.
 *
 * Features:
 * - Service Discovery with Eureka
 * - Feign Clients for inter-service communication
 * - REST API endpoints
 * - Database persistence with JPA
 * - Security integration
 * - Health monitoring with Actuator
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
@ComponentScan(basePackages = {
        "com.malaka.aat.internal",    // Scan internal module components
        "com.malaka.aat.core"          // Scan core module components (includes GlobalExceptionHandler)
})
public class MalakaInternalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MalakaInternalApplication.class, args);
    }
}
