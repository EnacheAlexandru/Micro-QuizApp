package org.quiztastic.questionservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String JWT_SECRET;

    Logger logger = LoggerFactory.getLogger(JwtService.class);

    private Claims extractClaims(String jwt, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey(key))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private Key getSignKey(String key) {
        if (key == null) {
            return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        }
        return Keys.hmacShaKeyFor(key.getBytes());
    }


    public String extractUsernameJwt(String jwt, String key) {
        return extractClaims(jwt, key).getSubject();
    }

    public String extractUsernameJwtBearer(String jwtBearer, String key) {
        return extractUsernameJwt(jwtBearer.substring(7), key);
    }

    public boolean isJwtBearerValid(String jwtBearer, String key) {
        if (jwtBearer == null) {
            logger.error("Invalid JWT header");
            return false;
        }

        try {
            extractClaims(jwtBearer.substring(7), key);
        } catch (Exception e) {
            logger.error("Invalid JWT value");
            return false;
        }

        return true;
    }
}
