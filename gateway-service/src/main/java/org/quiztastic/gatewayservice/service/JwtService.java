package org.quiztastic.gatewayservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.quiztastic.gatewayservice.config.SecurityConstants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    public String generateJwt(String username) {
        Date issueDate = new Date(System.currentTimeMillis());
        Date expireDate = new Date(issueDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(this.getSignKey());

        return jwtBuilder.compact();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes());
    }

    private Claims extractClaims(String jwt) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.getSignKey())
                .build();

        return jwtParser
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String extractUsername(String jwt) {
        return this.extractClaims(jwt).getSubject();
    }

    public boolean validateJwt(UserDetails user, String jwt) {
        Claims claims = this.extractClaims(jwt);
        boolean isExpired = claims.getExpiration().before(new Date(System.currentTimeMillis()));

        return !isExpired && user.getUsername().equals(claims.getSubject());
    }
}
