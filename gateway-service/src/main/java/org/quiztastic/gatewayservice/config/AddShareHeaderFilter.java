package org.quiztastic.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AddShareHeaderFilter implements GlobalFilter {

    @Value("${application.security.shared.secret-key.header}")
    private String SECRET_SHARED_HEADER;

    @Value("${application.security.shared.secret-key}")
    private String SECRET_SHARED;

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header(SECRET_SHARED_HEADER, "Bearer " + jwtService.generateJwt("gateway", SECRET_SHARED))
                .build();

        ServerWebExchange mutatedExchange = exchange
                .mutate()
                .request(request)
                .build();

        return chain.filter(mutatedExchange);
    }
}
