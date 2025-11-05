package com.malaka.aat.external.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        logger.info(
                "Incoming request: {}, {} from IP {}{}",
                method, path, clientIp, queryString != null ? queryString : ""
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration =  System.currentTimeMillis() - startTime;
            logger.info("Completed request: {} {} - Status: {} - Duration: {}ms",
                    method, path, response.getStatus(), duration
                    );
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return  xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return  xRealIp.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
