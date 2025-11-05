# Malaka AAT Microservices Project

A microservices-based application built with Spring Boot and Spring Cloud.

## Project Structure

This is a multi-module Maven project with a parent POM that manages dependencies and build configuration for all microservices.

```
MalakaAATWithMicroservices/
├── pom.xml (Parent POM)
├── service-registry/         (Eureka Server - Service Discovery)
├── config-server/            (Spring Cloud Config Server)
├── api-gateway/              (Spring Cloud Gateway)
└── [your-microservices]/     (Business microservices)
```

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Maven**: Build tool
- **Lombok**: Reduce boilerplate code
- **MapStruct**: Object mapping

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- Docker (optional, for containerization)

## Getting Started

### Creating a New Microservice

1. Create a new directory for your microservice in the project root
2. Add the module to the parent `pom.xml`:
   ```xml
   <modules>
       <module>your-service-name</module>
   </modules>
   ```
3. Create a `pom.xml` in your microservice directory with the parent reference:
   ```xml
   <parent>
       <groupId>com.malaka.aat</groupId>
       <artifactId>malaka-aat-parent</artifactId>
       <version>1.0.0-SNAPSHOT</version>
   </parent>
   ```

### Building the Project

```bash
# Build all modules
mvn clean install

# Build specific module
mvn clean install -pl module-name

# Skip tests
mvn clean install -DskipTests
```

### Running Services

```bash
# Run a specific service
cd service-name
mvn spring-boot:run
```

## Recommended Microservices Setup

For a complete microservices architecture, consider implementing:

1. **Service Registry** (Eureka Server)
   - Service discovery and registration
   - Port: 8761

2. **Config Server** (Spring Cloud Config)
   - Centralized configuration management
   - Port: 8888

3. **API Gateway** (Spring Cloud Gateway)
   - Single entry point for all microservices
   - Routing, load balancing, and security
   - Port: 8080

4. **Business Microservices**
   - User Service
   - Product Service
   - Order Service
   - etc.

## Configuration

Each microservice should have its own `application.yml` or `application.properties` file. For centralized configuration, use Spring Cloud Config Server.

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

[Specify your license here]
