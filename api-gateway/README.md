# API Gateway

Spring Cloud Gateway-based API Gateway for the Malaka AAT microservices architecture.

## Overview

The API Gateway serves as the single entry point for all client requests, providing:
- **Dynamic Routing**: Automatic route discovery from Eureka service registry
- **Load Balancing**: Client-side load balancing across service instances
- **Circuit Breaking**: Resilience4j integration for fault tolerance
- **Request/Response Filtering**: Custom filters for logging, headers, and transformations
- **CORS Configuration**: Centralized CORS policy management
- **Rate Limiting**: Request rate limiting support
- **Security**: Single point for authentication and authorization

## Architecture

```
┌──────────┐
│  Client  │
└────┬─────┘
     │
     ▼
┌────────────────────────────────────────┐
│         API Gateway (Port 8585)        │
│  ┌──────────────────────────────────┐  │
│  │  Filters (Logging, Headers, etc) │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │   Circuit Breaker (Resilience4j) │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │      Load Balancer (Ribbon)      │  │
│  └──────────────────────────────────┘  │
└────────┬───────────────────────────────┘
         │
    ┌────┴────┐
    │  Eureka │
    └────┬────┘
         │
    ┌────┼────────────┐
    │    │            │
┌───▼─────────┐ ┌─▼──┐ ┌▼───┐
│Malaka       │ │Svc │ │Svc │
│Internal     │ │ B  │ │ C  │
│(Port 8081)  │ └────┘ └────┘
└─────────────┘
```

## Quick Start

### Prerequisites

Ensure the Service Registry (Eureka Server) is running on port 8761.

### Running Locally

```bash
# From the api-gateway directory
mvn spring-boot:run

# Or from the parent directory
mvn spring-boot:run -pl api-gateway
```

The API Gateway will be available at: http://localhost:8585

### Running with Docker

```bash
# Build the Docker image
docker build -t api-gateway:v1.0 .

# Run the container (ensure service-registry is running)
docker run -d \
  --name api-gateway \
  -p 8585:8585 \
  --link service-registry:service-registry \
  api-gateway:v1.0
```

### Building the JAR

```bash
# From the api-gateway directory
mvn clean package

# Run the JAR
java -jar target/api-gateway-v1.0.jar
```

## Configuration

### Automatic Routing

The gateway routes requests to malaka-internal service based on API endpoint patterns:

```
All /api/** paths are routed to malaka-internal service

Examples:
- http://localhost:8585/api/auth/login → lb://malaka-internal/api/auth/login
- http://localhost:8585/api/user/me → lb://malaka-internal/api/user/me
- http://localhost:8585/api/course → lb://malaka-internal/api/course
- http://localhost:8585/api/spr/role → lb://malaka-internal/api/spr/role
```

### Manual Route Configuration

Custom routes are defined in `GatewayConfig.java`:

```java
.route("route-id", r -> r
    .path("/api/v1/**")
    .filters(f -> f.stripPrefix(2))
    .uri("lb://service-name"))
```

## Key Features

### 1. Global Filters

#### LoggingFilter (LoggingFilter.java:1)
- Logs all incoming requests and outgoing responses
- Measures request processing time
- Logs headers for debugging (debug level)

#### RequestHeaderFilter (RequestHeaderFilter.java:1)
- Adds `X-Correlation-ID` for distributed tracing
- Adds `X-Gateway` header to identify gateway requests
- Preserves existing correlation IDs

### 2. Circuit Breaker

Resilience4j circuit breaker configuration (application.yml:92):
- **Sliding Window Size**: 10 requests
- **Failure Rate Threshold**: 50%
- **Wait Duration**: 10 seconds in open state
- **Half-Open Calls**: 3 permitted calls
- **Timeout**: 3 seconds per request

### 3. CORS Configuration

Global CORS settings (application.yml:14):
- Allowed origins: All (configurable)
- Allowed methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Credentials: Disabled by default
- Max age: 3600 seconds

### 4. Rate Limiting

Redis-based rate limiting (requires Redis):
- Replenish rate: 10 requests/second
- Burst capacity: 20 requests
- Configure in application.yml:48

### 5. Retry Mechanism

Automatic retry configuration (application.yml:43):
- Retries: 3 attempts
- Methods: GET, POST
- Conditions: Server errors, IOException, TimeoutException

## Routing Examples

### Access Registered Services

```bash
# List all registered services in Eureka
curl http://localhost:8585/eureka/apps

# Authentication endpoints
curl -X POST http://localhost:8585/api/auth/register
curl -X POST http://localhost:8585/api/auth/login

# User management
curl http://localhost:8585/api/user/me
curl http://localhost:8585/api/user

# Course management
curl http://localhost:8585/api/course
curl -X POST http://localhost:8585/api/course

# Module and Topic management
curl http://localhost:8585/api/module/{id}
curl http://localhost:8585/api/topic/{topicId}/lecture

# Test management
curl -X POST http://localhost:8585/api/test

# Student applications
curl -X POST http://localhost:8585/api/application/individual

# File management
curl http://localhost:8585/api/file/{id}

# Citizen information (E-Gov integration)
curl http://localhost:8585/api/info/{pinfl}

# Reference data (SPR)
curl http://localhost:8585/api/spr/role
curl http://localhost:8585/api/spr/faculty
```

### Test Circuit Breaker

```bash
# When service is down, fallback is triggered
curl http://localhost:8585/unavailable-service/api/endpoint
# Returns 503 with fallback message
```

### Check Gateway Health

```bash
# Health endpoint
curl http://localhost:8585/actuator/health

# Gateway routes
curl http://localhost:8585/actuator/gateway/routes

# Circuit breaker status
curl http://localhost:8585/actuator/circuitbreakers
```

## Best Practices Implemented

### 1. Reactive Programming
- Uses Spring WebFlux for non-blocking I/O
- Supports high concurrency with low resource usage

### 2. Service Discovery Integration
- Automatic route creation from Eureka
- Dynamic load balancing across instances

### 3. Resilience Patterns
- Circuit breaker for fault isolation
- Retry mechanism for transient failures
- Timeout configuration for slow services

### 4. Observability
- Request/response logging with correlation IDs
- Health checks via Actuator
- Metrics exposure for monitoring

### 5. Security Best Practices
- Non-root user in Docker container
- CORS configuration for cross-origin requests
- Custom headers for request tracking

### 6. Performance Optimization
- JVM optimization for containers
- Connection pooling (Netty)
- Response caching capabilities

## Monitoring Endpoints

| Endpoint | Description |
|----------|-------------|
| http://localhost:8585/actuator/health | Health status |
| http://localhost:8585/actuator/info | Application info |
| http://localhost:8585/actuator/metrics | Metrics data |
| http://localhost:8585/actuator/gateway/routes | All gateway routes |
| http://localhost:8585/actuator/circuitbreakers | Circuit breaker states |
| http://localhost:8585/actuator/circuitbreakerevents | Circuit breaker events |

## Troubleshooting

### Gateway not routing to services

1. Check if Eureka Server is running: http://localhost:8761
2. Verify service is registered in Eureka
3. Check gateway logs for routing errors
4. Verify service URL format: `http://localhost:8585/{service-name}/...` or `http://localhost:8585/api/internal/...`

### Circuit breaker always open

1. Check if downstream service is healthy
2. Review failure threshold configuration
3. Adjust wait duration or failure rate
4. Check logs for specific errors

### CORS errors

1. Update `allowedOrigins` in application.yml:17
2. Enable credentials if needed
3. Add required headers to `allowedHeaders`
4. Check browser network tab for preflight requests

### Rate limiting not working

1. Ensure Redis is installed and running
2. Configure Redis connection in application.yml
3. Adjust rate limiter parameters
4. Remove rate limiter filter if not needed

## Advanced Configuration

### Enable Redis Rate Limiting

1. Install Redis:
```bash
docker run -d -p 6379:6379 redis:alpine
```

2. Add Redis configuration to application.yml:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### Custom Route Filters

Add custom filters in `GatewayConfig.java`:

```java
.route("custom-route", r -> r
    .path("/api/**")
    .filters(f -> f
        .addRequestHeader("X-Custom-Header", "value")
        .addResponseHeader("X-Response-Time", "...")
        .rewritePath("/api/(?<segment>.*)", "/${segment}"))
    .uri("lb://service-name"))
```

### Authentication Integration

For JWT authentication:

1. Add Spring Security dependency
2. Create authentication filter
3. Validate tokens in filter
4. Pass user context to downstream services

## Dependencies

- Spring Boot 3.2.0
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Resilience4j Circuit Breaker
- Spring Boot Actuator
- Lombok

## Ports

- **8585**: API Gateway HTTP port

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Gateway port | 8585 |
| EUREKA_URI | Eureka server URL | http://localhost:8761/eureka |
| SPRING_PROFILES_ACTIVE | Active profile | default |

## Registered Services and Routes

The gateway routes to the following services:

| Service | Port | Route Pattern | Controller | Example |
|---------|------|---------------|------------|---------|
| malaka-internal | 8081 | /api/auth/** | AuthController | http://localhost:8585/api/auth/login |
| malaka-internal | 8081 | /api/user/** | UserController | http://localhost:8585/api/user/me |
| malaka-internal | 8081 | /api/course/** | CourseController | http://localhost:8585/api/course |
| malaka-internal | 8081 | /api/module/** | ModuleController | http://localhost:8585/api/module/{id} |
| malaka-internal | 8081 | /api/topic/** | TopicController | http://localhost:8585/api/topic/{id}/lecture |
| malaka-internal | 8081 | /api/test/** | TestController | http://localhost:8585/api/test |
| malaka-internal | 8081 | /api/application/** | StudentApplicationController | http://localhost:8585/api/application/individual |
| malaka-internal | 8081 | /api/file/** | FileController | http://localhost:8585/api/file/{id} |
| malaka-internal | 8081 | /api/info/** | InfoController | http://localhost:8585/api/info/{pinfl} |
| malaka-internal | 8081 | /api/spr/** | SPR Controllers | http://localhost:8585/api/spr/role |

### SPR (Reference Data) Endpoints

| Route Pattern | Controller | Description |
|---------------|------------|-------------|
| /api/spr/role | RoleController | User roles management |
| /api/spr/faculty | FacultySprController | Faculty reference data |
| /api/spr/department | DepartmentSprController | Department reference data |
| /api/spr/lang | LangSprController | Language reference data |
| /api/spr/course-type | CourseTypeSprController | Course types |
| /api/spr/course-format | CourseFormatSprController | Course formats |
| /api/spr/course-student-type | CourseStudentTypeSprController | Student types |

## Next Steps

1. Add authentication/authorization (Spring Security + JWT)
2. Implement distributed tracing (Zipkin/Sleuth)
3. Add API documentation (SpringDoc OpenAPI)
4. Configure centralized logging (ELK Stack)
5. Set up monitoring dashboards (Prometheus + Grafana)
6. Implement API rate limiting with Redis
7. Add request/response caching

## References

- [Spring Cloud Gateway Documentation](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/)
- [Reactive Programming Guide](https://projectreactor.io/docs)
