package org.quiztastic.gatewayservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String JWT_SECRET;

    @Value("${application.security.jwt.expiration}")
    private long JWT_EXPIRATION;

    public String generateJwt(String username, String key) {
        Date issueDate = new Date(System.currentTimeMillis());
        Date expireDate = new Date(issueDate.getTime() + JWT_EXPIRATION);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(getSignKey(key));

        return jwtBuilder.compact();
    }

    private Key getSignKey(String key) {
        if (key == null) {
            return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        }
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    private Claims extractClaims(String jwt, String key) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSignKey(key))
                .build();

        return jwtParser
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String extractUsername(String jwt, String key) {
        return this.extractClaims(jwt, key).getSubject();
    }

    public boolean validateJwt(UserDetails user, String jwt, String key) {
        Claims claims = this.extractClaims(jwt, key);
        boolean isExpired = claims.getExpiration().before(new Date(System.currentTimeMillis()));

        return !isExpired && user.getUsername().equals(claims.getSubject());
    }
}
