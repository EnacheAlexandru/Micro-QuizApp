package org.quiztastic.questionservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.service.JwtService;
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
    private String SHARED_SECRET_HEADER;

    @Value("${application.security.shared.secret-key}")
    private String SHARED_SECRET;

    private final JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String sharedValue = httpRequest.getHeader(SHARED_SECRET_HEADER);

        if (sharedValue == null) {
            logger.error("Invalid shared header");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!sharedValue.equals(SHARED_SECRET)) {
            logger.error("Invalid shared value");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwtValue = httpRequest.getHeader("Authorization");

        if (!jwtService.isJwtHeaderValid(jwtValue)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}