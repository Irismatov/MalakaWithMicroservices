# Malaka AAT Core

Core library containing shared utilities, DTOs, exceptions, and common classes used across all microservices.

## Overview

This is **not a runnable service** but a shared library that other microservices depend on. It provides common functionality to ensure consistency across the system.

## Structure

```
malaka-aat-core/
├── src/main/java/com/malaka/aat/core/
│   ├── dto/              # Data Transfer Objects
│   ├── exception/        # Custom exceptions
│   ├── util/             # Utility classes
│   └── constant/         # Application constants
```

## What's Included

### DTOs (Data Transfer Objects)
- **ApiResponse** - Standard response wrapper for all APIs
- **ErrorDetails** - Structured error information
- **PageResponse** - Generic paginated response

### Exceptions
- **BaseException** - Base class for all custom exceptions
- **ResourceNotFoundException** - For 404 errors
- **BadRequestException** - For 400 errors

### Utilities
- **DateTimeUtils** - Date/time helper methods

### Constants
- **AppConstants** - Application-wide constants

## Usage in Other Services

### 1. Add Dependency

In your microservice's `pom.xml`:

```xml
<dependency>
    <groupId>com.malaka.aat</groupId>
    <artifactId>malaka-aat-core</artifactId>
    <version>v1.0</version>
</dependency>
```

### 2. Use ApiResponse

```java
@GetMapping("/users/{id}")
public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
    UserDTO user = userService.findById(id);
    return ApiResponse.success(user);
}
```

### 3. Use Exceptions

```java
public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
}
```

### 4. Use PageResponse

```java
@GetMapping("/users")
public ApiResponse<PageResponse<UserDTO>> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {

    List<UserDTO> users = userService.findAll(page, size);
    PageResponse<UserDTO> pageResponse = PageResponse.of(users, page, size, totalCount);

    return ApiResponse.success(pageResponse);
}
```

## Building

```bash
# From the core directory
mvn clean install

# This will install the JAR to your local Maven repository
# Other services can then depend on it
```

## Adding Your Own Classes

Feel free to add your own:
- DTOs in `dto/` package
- Custom exceptions in `exception/` package
- Utility methods in `util/` package
- Constants in `constant/` package

## Best Practices

1. Keep this module **dependency-light** - avoid heavy frameworks
2. Only include truly **shared** code - not service-specific logic
3. Use **immutable DTOs** where possible (use `@Value` or builders)
4. Document all public APIs with JavaDoc
5. Keep utility classes **stateless** with private constructors

## Dependencies

- Lombok (for reducing boilerplate)
- Jackson (for JSON processing)
- Jakarta Validation API
- Apache Commons Lang
- SLF4J (for logging)
