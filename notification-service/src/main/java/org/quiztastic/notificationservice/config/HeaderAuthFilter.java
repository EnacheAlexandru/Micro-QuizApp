package org.quiztastic.notificationservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.quiztastic.notificationservice.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HeaderAuthFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(HeaderAuthFilter.class);

    private final JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Map<String, String> paramMap = new HashMap<>();
        if (httpRequest.getQueryString() != null) {
            try {
                paramMap = getParamMap(httpRequest.getQueryString());
            } catch (Exception e) {
                logger.error("Invalid URL parameters");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        String jwtBearer;
        if (paramMap.containsKey("token")) {
            jwtBearer = "Bearer " + paramMap.get("token");
        } else {
            jwtBearer = httpRequest.getHeader("Authorization");
        }

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

    private Map<String, String> getParamMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
}