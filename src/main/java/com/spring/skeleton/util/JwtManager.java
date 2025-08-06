package com.spring.skeleton.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtManager {

    private final Key key;
    private final Long expiration;
    private final Long refreshExpiration;

    public JwtManager(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") Long expiration,
                      @Value("${jwt.expiration.refresh}") Long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generate(String email, Long id, Boolean isRefreshToken) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (isRefreshToken ? refreshExpiration : expiration)))
                .signWith(key)
                .compact();
    }

    public String generate(String email, Long id, Long customExpiration) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + customExpiration))
                .signWith(key)
                .compact();
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getId(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    public boolean isValid(String token) {
        return getSubject(token) != null && !isTokenExpired(token);
    }

    public Instant getExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(actualToken)
                .getBody();
    }
}
