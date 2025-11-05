package com.malaka.aat.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Service Registry Application using Netflix Eureka Server.
 * This service acts as a discovery server where all microservices register themselves.
 *
 * Best Practices Implemented:
 * - @EnableEurekaServer activates Eureka Server functionality
 * - Standalone mode configuration in application.yml
 * - Actuator endpoints enabled for health monitoring
 * - Default port 8761 (Eureka standard)
 */
@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = {
        "com.malaka.aat.serviceregistry",  // Scan service registry components
        "com.malaka.aat.core"               // Scan core module components (includes GlobalExceptionHandler)
})
public class ServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
