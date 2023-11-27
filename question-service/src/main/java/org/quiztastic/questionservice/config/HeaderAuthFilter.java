package org.quiztastic.questionservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.quiztastic.questionservice.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HeaderAuthFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(HeaderAuthFilter.class);

    @Value("${application.security.shared.secret-key.header}")
    private String SHARED_SECRET_HEADER;

    @Value("${application.security.shared.secret-key}")
    private String SHARED_SECRET;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String value = httpRequest.getHeader(SHARED_SECRET_HEADER);

        if (value == null) {
            logger.error("Invalid shared header");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!value.equals(SHARED_SECRET)) {
            logger.error("Invalid shared key");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}