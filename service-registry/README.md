# Service Registry (Eureka Server)

Netflix Eureka-based service discovery server for the Malaka AAT microservices architecture.

## Overview

The Service Registry is a critical infrastructure component that enables:
- **Service Discovery**: Microservices automatically register and discover each other
- **Load Balancing**: Client-side load balancing across service instances
- **Health Monitoring**: Automatic health checks and service instance management
- **Fault Tolerance**: Self-preservation mode to handle network issues

## Architecture

```
┌─────────────────────────────────────────────┐
│         Service Registry (Eureka)           │
│              Port: 8761                     │
└─────────────────┬───────────────────────────┘
                  │
        ┌─────────┼─────────┐
        │         │         │
   ┌────▼───┐ ┌──▼────┐ ┌──▼─────┐
   │Service │ │Service│ │Service │
   │   A    │ │   B   │ │   C    │
   └────────┘ └───────┘ └────────┘
```

## Quick Start

### Running Locally

```bash
# From the service-registry directory
mvn spring-boot:run

# Or from the parent directory
mvn spring-boot:run -pl service-registry
```

The Eureka Dashboard will be available at: http://localhost:8761

### Running with Docker

```bash
# Build the Docker image
docker build -t service-registry:v1.0 .

# Run the container
docker run -d \
  --name service-registry \
  -p 8761:8761 \
  service-registry:v1.0
```

### Building the JAR

```bash
# From the service-registry directory
mvn clean package

# Run the JAR
java -jar target/service-registry-v1.0.jar
```

## Configuration

### Default Configuration (application.yml)

- **Port**: 8761 (Eureka standard)
- **Hostname**: localhost
- **Self-registration**: Disabled (best practice for standalone)
- **Self-preservation**: Enabled (protects against network partitions)

### Docker Configuration (application-docker.yml)

Activated with `--spring.profiles.active=docker`:
- **Hostname**: service-registry (for Docker networking)
- **Prefer IP**: true (for container environments)
- **Debug logging**: Enabled

## Best Practices Implemented

### 1. Standalone Configuration
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```
The server doesn't register itself as a client, reducing overhead.

### 2. Self-Preservation Mode
```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
```
Protects against mass evictions during network issues.

### 3. Health Monitoring
- Spring Boot Actuator integrated
- Health endpoint: http://localhost:8761/actuator/health
- Custom health checks in Docker container

### 4. Security Considerations
- Non-root user in Docker container
- Minimal base image (Alpine Linux)
- No hardcoded credentials

### 5. Performance Optimization
- Response cache update interval: 30 seconds
- Eviction interval: 60 seconds
- Fast startup with `wait-time-in-ms-when-sync-empty: 0`

## Registering Client Services

To register a microservice with Eureka, add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

And in your service's `application.yml`:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${random.value}
```

## Monitoring Endpoints

| Endpoint | Description |
|----------|-------------|
| http://localhost:8761 | Eureka Dashboard |
| http://localhost:8761/eureka/apps | All registered services (XML) |
| http://localhost:8761/actuator/health | Health check |
| http://localhost:8761/actuator/info | Application info |
| http://localhost:8761/actuator/metrics | Metrics |

## Troubleshooting

### Services not showing up in Eureka

1. Check if the client service is running
2. Verify the `defaultZone` URL in client configuration
3. Check network connectivity between services
4. Review logs for registration errors

### Self-Preservation Mode Activated

This is normal during development. The message means:
- Eureka detected fewer renewals than expected
- Services won't be evicted to prevent false positives
- Can be disabled for testing (not recommended for production)

### High Memory Usage

Eureka caches service information. To reduce memory:
- Adjust `response-cache-update-interval-ms`
- Increase `eviction-interval-timer-in-ms`
- Monitor with JVM flags: `-Xmx512m -Xms256m`

## Dependencies

- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Netflix Eureka Server
- Spring Boot Actuator

## Ports

- **8761**: Eureka Server HTTP port (default)

## Next Steps

After setting up the Service Registry:

1. Create a Config Server for centralized configuration
2. Set up an API Gateway for routing
3. Create your business microservices
4. Implement Resilience4j for circuit breakers
5. Add distributed tracing with Zipkin/Sleuth

## References

- [Spring Cloud Netflix Documentation](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/)
- [Eureka Wiki](https://github.com/Netflix/eureka/wiki)
- [Microservices Best Practices](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
