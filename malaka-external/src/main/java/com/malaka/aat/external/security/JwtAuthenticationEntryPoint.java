package com.malaka.aat.external.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom authentication entry point for handling unauthorized access attempts.
 * This is triggered when a user tries to access a protected resource without proper authentication.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Log unauthorized access attempt with detailed information
        String reason = determineUnauthorizedReason(authException);

        // Log to standard logger
        log.warn("Unauthorized access attempt - Path: {} | Method: {} | IP: {} | Reason: {}",
                request.getRequestURI(),
                request.getMethod(),
                getClientIp(request),
                reason);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("resultCode", 10);
        body.put("resultNote", "Ruxsat cheklangan");


        Map<String, Object> data = new HashMap<>();
        data.put("method", request.getMethod());
        data.put("status", "403 FORBIDDEN");
        data.put("path", request.getRequestURI());
        data.put("message", "Access Denied");
        data.put("timestamp", System.currentTimeMillis());

        body.put("data", data);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    /**
     * Determine the specific reason for unauthorized access
     */
    private String determineUnauthorizedReason(AuthenticationException authException) {
        if (authException == null) {
            return "No authentication provided";
        }

        String exceptionClass = authException.getClass().getSimpleName();
        String message = authException.getMessage();

        return switch (exceptionClass) {
            case "InsufficientAuthenticationException" -> "Missing or invalid authentication token";
            case "BadCredentialsException" -> "Invalid credentials provided";
            case "DisabledException" -> "User account is disabled";
            case "LockedException" -> "User account is locked";
            default -> message != null ? message : "Authentication failed";
        };
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
