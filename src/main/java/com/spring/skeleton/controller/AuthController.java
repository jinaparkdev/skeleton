package com.spring.skeleton.controller;

import com.spring.skeleton.model.AuthResponse;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.service.CustomAuthenticationManager;
import com.spring.skeleton.util.JwtManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final CustomAuthenticationManager authManager;
    private final JwtManager jwtManager;
    private final RedisTemplate<String, Object> redis;
    @Value("${jwt.expiration.refresh}")
    private Long refreshExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    @Data
    public static class Body {
        @Email(message = "유효하지 않은 이메일입니다.")
        private String email;
        //TODO 비밀번호 정책 추가
        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticate(HttpServletResponse response,
                                                     @RequestBody Body body) {

        AuthResponse auth = authManager.authenticate(body.getEmail(), body.getPassword());
        Company company = auth.getCompany();

        String refreshToken = jwtManager.generate(company.getEmail(), company.getId(), true);
        long timeout = refreshExpiration / 1000; //밀리초를 초로 변환

        redis.opsForValue().set(
                REFRESH_TOKEN_PREFIX + company.getId(),
                refreshToken,
                timeout,
                TimeUnit.SECONDS
                               );

        response.addHeader(
                "Set-Cookie",
                "refreshToken=" + refreshToken + "; HttpOnly; Path=/; SameSite=Strict"
                          );

        return ResponseEntity.ok(auth);
    }

    @PostMapping("/auth/current")
    public ResponseEntity<AuthResponse> current(HttpServletRequest request) {

        Long companyId = (Long) request.getAttribute("companyId");
        String refreshToken = (String) redis.opsForValue().get(REFRESH_TOKEN_PREFIX + companyId);

        if (refreshToken != null) {
            AuthResponse auth = authManager.authenticate(companyId);
            return ResponseEntity.ok(auth);
        }

        return ResponseEntity.status(401).build();
    }
}
