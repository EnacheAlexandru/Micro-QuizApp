package org.quiztastic.questionservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secret-key}")
    private String JWT_SECRET;

    @Value("${application.security.share.secret-key}")
    private String SHARE_SECRET;

    private Claims extractClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String extractUsername(String jwt) {
        return extractClaims(jwt).getSubject();
    }

    public String extractUsernameHeader(String jwtHeader) {
        return extractUsername(jwtHeader.substring(7));
    }

    public boolean isHeaderValid(String jwtHeader, String shareHeader) {
        return isJwtHeaderValid(jwtHeader) && isShareHeaderValid(shareHeader);
    }

    public boolean isJwtHeaderValid(String jwtHeader) {
        if (jwtHeader == null) {
            logger.error("Invalid JWT header");
            return false;
        }

        try {
            extractClaims(jwtHeader.substring(7));
        } catch (Exception e) {
            logger.error("Invalid JWT header");
            return false;
        }

        return true;
    }

    public boolean isShareHeaderValid(String shareHeader) {
        if (shareHeader == null || !shareHeader.equals(SHARE_SECRET)) {
            logger.error("Invalid share header");
            return false;
        }
        return true;
    }
}
