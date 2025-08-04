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
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generate(String email, Long id, Boolean isRefreshToken) {

        Long expiration = isRefreshToken ? refreshExpiration : this.expiration;

        return Jwts.builder()
                .setSubject(email) // 이메일 기준으로 토큰 생성
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(subtractBearer(token))
                .getBody();
        return claims.getSubject();
    }

    public Long getId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(subtractBearer(token))
                .getBody();
        return claims.get("id", Long.class);
    }

    public boolean isValid(String token) {
        return getSubject(token) != null && !isTokenExpired(token);
    }

    public Instant getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(subtractBearer(token))
                .getBody();
        return claims.getExpiration().toInstant();
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
