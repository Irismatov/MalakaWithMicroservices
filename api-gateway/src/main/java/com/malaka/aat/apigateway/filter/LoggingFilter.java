package com.malaka.aat.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;


@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Instant startTime = Instant.now();

        log.info(">>> Incoming Request: {} {} from {}",
                request.getMethod(),
                request.getURI(),
                request.getRemoteAddress());

        log.info(">>> Request Headers: {}", request.getHeaders());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);

                    log.info("<<< Outgoing Response: Status {} for {} {} - Duration: {}ms",
                            response.getStatusCode(),
                            request.getMethod(),
                            request.getURI(),
                            duration.toMillis());

                    log.debug("<<< Response Headers: {}", response.getHeaders());
                }));
    }

    @Override
    public int getOrder() {
        // Execute this filter first (lowest order)
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
