package com.malaka.aat.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter for adding custom headers to requests.
 * Adds correlation ID for distributed tracing and gateway identification.
 *
 * Best Practices:
 * - Adds X-Correlation-ID for request tracking across microservices
 * - Adds X-Gateway header to identify requests coming from gateway
 * - Preserves existing correlation ID if present
 */
@Slf4j
@Component
public class RequestHeaderFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String GATEWAY_HEADER = "X-Gateway";
    private static final String GATEWAY_NAME = "Malaka-AAT-Gateway";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Get or generate correlation ID
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated new correlation ID: {}", correlationId);
        } else {
            log.debug("Using existing correlation ID: {}", correlationId);
        }

        // Add custom headers to the request
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .header(GATEWAY_HEADER, GATEWAY_NAME)
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        // Execute after logging filter
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
