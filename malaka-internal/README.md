# Malaka Internal Service

Internal business microservice for the Malaka AAT system.

## Overview

This microservice provides internal business logic and APIs for the Malaka AAT platform. It is registered with Eureka Service Registry and accessible via the API Gateway.

## Features

- REST API endpoints
- Database persistence with JPA/Hibernate
- Service discovery with Eureka
- Health monitoring with Actuator
- Uses malaka-aat-core shared library
- Security integration ready

## Technology Stack

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Cloud Netflix Eureka Client
- PostgreSQL (configurable)
- Lombok

## Project Structure

```
malaka-internal/
├── src/main/java/com/malaka/aat/internal/
│   ├── controller/       # REST Controllers
│   ├── service/          # Business logic services
│   ├── repository/       # JPA repositories
│   ├── security/         # Security configurations
│   ├── config/           # Application configurations
│   ├── dto/              # Data Transfer Objects
│   └── util/             # Utility classes
├── src/main/resources/
│   ├── application.yml
│   └── application-docker.yml
└── pom.xml
```

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- PostgreSQL database
- Service Registry running on port 8761

## Database Setup

### PostgreSQL

```bash
# Create database
createdb malaka_internal_db

# Or using psql
psql -U postgres
CREATE DATABASE malaka_internal_db;
```

Update credentials in `application.yml` if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/malaka_internal_db
    username: your_username
    password: your_password
```

## Running the Service

### Locally

```bash
# From the malaka-internal directory
mvn spring-boot:run

# Or from the parent directory
mvn spring-boot:run -pl malaka-internal
```

The service will start on: http://localhost:8081

### With Docker

```bash
# Build the JAR
mvn clean package

# Run with Docker profile
java -jar target/malaka-internal-v1.0.jar --spring.profiles.active=docker
```

## Accessing the Service

### Direct Access
- Base URL: http://localhost:8081
- Health Check: http://localhost:8081/actuator/health

### Via API Gateway
- Base URL: http://localhost:8080/malaka-internal
- Health Check: http://localhost:8080/malaka-internal/actuator/health

## Dependencies

### Core Module
This service depends on `malaka-aat-core` which provides:
- Common DTOs (ApiResponse, PageResponse, etc.)
- Common exceptions
- Utility classes
- Constants

### Using Core Module Classes

```java
import com.malaka.aat.core.dto.ApiResponse;
import com.malaka.aat.core.exception.ResourceNotFoundException;

@GetMapping("/{id}")
public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    return ApiResponse.success(userMapper.toDto(user));
}
```

## Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| server.port | 8081 | Service port |
| spring.application.name | malaka-internal | Service name in Eureka |
| eureka.client.service-url.defaultZone | http://localhost:8761/eureka/ | Eureka server URL |

## Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| /actuator/health | Health status |
| /actuator/info | Application info |
| /actuator/metrics | Metrics data |

## Development

### Adding a New Controller

```java
package com.malaka.aat.internal.controller;

import com.malaka.aat.core.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {

    @GetMapping
    public ApiResponse<String> example() {
        return ApiResponse.success("Hello from Malaka Internal!");
    }
}
```

### Adding a New Entity

```java
package com.malaka.aat.internal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "example")
@Data
public class Example {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```

### Adding a Repository

```java
package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepository extends JpaRepository<Example, Long> {
}
```

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn clean test jacoco:report
```

## Building

```bash
# Build JAR
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

## Troubleshooting

### Cannot connect to Eureka

1. Verify Service Registry is running: http://localhost:8761
2. Check `eureka.client.service-url.defaultZone` in application.yml
3. Check network connectivity

### Database connection errors

1. Verify PostgreSQL is running
2. Check database exists: `malaka_internal_db`
3. Verify credentials in application.yml
4. Check database URL and port

### Port already in use

Change the port in application.yml:
```yaml
server:
  port: 8082  # or any available port
```

## Next Steps

1. Add your business entities and repositories
2. Implement service layer logic
3. Create REST controllers
4. Add security configurations
5. Write unit and integration tests
6. Document your APIs with Swagger/OpenAPI

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
