package org.quiztastic.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class SharedHeaderFilter implements GlobalFilter {

    @Value("${application.security.shared-key}")
    private String SHARED_SECRET;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-Shared-Header", SHARED_SECRET)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();

        return chain.filter(mutatedExchange);
    }
}
