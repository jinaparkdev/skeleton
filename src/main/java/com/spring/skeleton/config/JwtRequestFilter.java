package com.spring.skeleton.config;

import com.spring.skeleton.service.CustomAuthenticationManager;
import com.spring.skeleton.util.JwtManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtManager manager;
    private final CustomAuthenticationManager userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain) throws ServletException, IOException {
        log.info("[JWT] doFilterInternal 호출됨. uri={}, method={}, shouldNotFilter={}",
                request.getRequestURI(), request.getMethod(), shouldNotFilter(request));

        final String token = request.getHeader("Authorization");
        log.info("[JWT] 인증 헤더: {}", token);
        if (token == null || !token.startsWith("Bearer")) {
            log.warn("[JWT] JWT 토큰이 없거나 형식이 잘못되었습니다: {}", token);
            throw new AuthenticationCredentialsNotFoundException("JWT Token is missing");
        }

        try {
            String subject = manager.getSubject(token);
            log.info("[JWT] Token subject: {}", subject);
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            if (manager.isValid(token)) {
                log.info("[JWT] 유효한 토큰 : {}", subject);
                UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(upat);
            }

            Long companyId = manager.getId(token);
            request.setAttribute("companyId", companyId);

        } catch (Exception e) {
            log.error("[JWT] JWT 토큰 유효하지 않음: {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("유효하지 않은 JWT 토큰: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        boolean result = new OrRequestMatcher(
                new AntPathRequestMatcher("/auth"),
                new AntPathRequestMatcher("/company/**", "POST"),
                new AntPathRequestMatcher("/company/**", "HEAD")
        ).matches(request);
        log.info("[JWT] shouldNotFilter 호출됨. uri={}, method={}, result={}",
                request.getRequestURI(), request.getMethod(), result);
        return result;
    }
}
