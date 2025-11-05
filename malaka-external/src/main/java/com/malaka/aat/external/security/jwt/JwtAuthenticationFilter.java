package com.malaka.aat.external.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.external.security.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String jwt = null;

        try {
            jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                long processingTime = System.currentTimeMillis() - startTime;

                // Log successful JWT authentication
                log.debug("JWT authentication successful for user: {} on path: {} ({}ms)",
                        userDetails.getUsername(),
                        request.getRequestURI(),
                        processingTime);

            } else if (StringUtils.hasText(jwt)) {
                // JWT present but invalid
                log.warn("Invalid JWT token on path: {} from IP: {}", request.getRequestURI(), getClientIp(request));
            } else if (!isWhitelistedPath(request.getRequestURI())) {
                // No JWT provided for protected endpoint
                log.debug("No JWT token found for protected endpoint: {} from IP: {}",
                        request.getRequestURI(),
                        getClientIp(request));
            }
        } catch (ExpiredJwtException ex) {
            String username = ex.getClaims() != null ? ex.getClaims().getSubject() : "UNKNOWN";
            log.warn("Expired JWT token for user: {} on path: {} from IP: {}",
                    username, request.getRequestURI(), getClientIp(request));

            // Return 401 with expired token message
            handleExpiredToken(request, response, username);
            return; // Don't continue the filter chain
        } catch (io.jsonwebtoken.SignatureException ex) {
            log.warn("Invalid JWT signature on path: {} from IP: {}", request.getRequestURI(), getClientIp(request));
            handleInvalidToken(request, response, "Invalid JWT signature", "Signature Invalid");
            return;
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token on path: {} from IP: {}", request.getRequestURI(), getClientIp(request));
            handleInvalidToken(request, response, "Malformed JWT token", "Token Malformed");
            return;
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token on path: {} from IP: {}", request.getRequestURI(), getClientIp(request));
            handleInvalidToken(request, response, "Unsupported JWT token", "Token Unsupported");
            return;
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty on path: {}", request.getRequestURI());
            handleInvalidToken(request, response, "JWT claims string is empty", "Invalid Claims");
            return;
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context for path: {} - Error: {}",
                    request.getRequestURI(), ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        // Get token from Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Check if the request path is whitelisted (no authentication required)
     */
    private boolean isWhitelistedPath(String path) {
        String[] whiteList = {
                "/api/auth/",
                "/api/info/",
                "/api/application/",
                "/h2-console/",
                "/swagger-ui/",
                "/v3/api-docs"
        };

        for (String whiteListedPath : whiteList) {
            if (path.contains(whiteListedPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract client IP address from request, considering proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Handle expired JWT token by returning 401 Unauthorized with proper message
     */
    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("resultCode", 11); // JWT_ERROR from ResponseStatus
        body.put("resultNote", "JWT token bilan xatolik yuzaga keldi");

        Map<String, Object> data = new HashMap<>();
        data.put("method", request.getMethod());
        data.put("status", "401 UNAUTHORIZED");
        data.put("path", request.getRequestURI());
        data.put("message", "JWT token has expired. Please login again.");
        data.put("error", "Token Expired");
        data.put("timestamp", System.currentTimeMillis());

        body.put("data", data);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    /**
     * Handle invalid JWT token by returning 401 Unauthorized with proper message
     */
    private void handleInvalidToken(HttpServletRequest request, HttpServletResponse response, String message, String error) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("resultCode", 11); // JWT_ERROR from ResponseStatus
        body.put("resultNote", "JWT token bilan xatolik yuzaga keldi");

        Map<String, Object> data = new HashMap<>();
        data.put("method", request.getMethod());
        data.put("status", "401 UNAUTHORIZED");
        data.put("path", request.getRequestURI());
        data.put("message", message);
        data.put("error", error);
        data.put("timestamp", System.currentTimeMillis());

        body.put("data", data);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

}
