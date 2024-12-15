package com.spring.skeleton.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtManager {

    private final Key key;
    private final Long expiration;

    public JwtManager(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") Long expiration) {
        this.expiration = expiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, Long id) {

        return Jwts.builder()
                .setSubject(email) // Subject is the email of the company
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(subtractBearer(token))
                .getBody();
        return claims.getSubject();
    }

    public Long getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(subtractBearer(token))
                .getBody();
        return claims.get("id", Long.class);
    }

    public boolean validateToken(String token) {
        return getSubjectFromToken(token) != null && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(subtractBearer(token))
                .getBody();
        return claims.getExpiration().before(new Date());
    }

    private String subtractBearer(String token) {
        return token.substring(7);
    }
}
