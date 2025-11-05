package com.malaka.aat.external;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Malaka External Service Application.
 *
 * This microservice provides external-facing APIs and integrations.
 * It is registered with Eureka Service Registry and accessible via API Gateway.
 *
 * Features:
 * - Service Discovery with Eureka
 * - Feign Clients for inter-service communication
 * - REST API endpoints for external consumers
 * - Integration with external systems
 * - Security integration
 * - Health monitoring with Actuator
 */
@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {
        "com.malaka.aat.external",    // Scan external module components
        "com.malaka.aat.core"          // Scan core module components (includes GlobalExceptionHandler)
})
public class MalakaExternalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MalakaExternalApplication.class, args);
    }
}
