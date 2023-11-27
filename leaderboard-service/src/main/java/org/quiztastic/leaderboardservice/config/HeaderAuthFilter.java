package org.quiztastic.leaderboardservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.quiztastic.leaderboardservice.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class HeaderAuthFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(HeaderAuthFilter.class);

    @Value("${application.security.shared.secret-key.header}")
    private String SECRET_SHARED_HEADER;

    @Value("${application.security.shared.secret-key}")
    private String SECRET_SHARED;

    private final JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String sharedBearer = httpRequest.getHeader(SECRET_SHARED_HEADER);

        if (sharedBearer == null) {
            logger.error("Invalid shared header");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!jwtService.isJwtBearerValid(sharedBearer, SECRET_SHARED)) {
            logger.error("Invalid shared value");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwtBearer = httpRequest.getHeader("Authorization");

        if (jwtBearer == null) {
            logger.error("Invalid user token header");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!jwtService.isJwtBearerValid(jwtBearer, null)) {
            logger.error("Invalid user token value");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}