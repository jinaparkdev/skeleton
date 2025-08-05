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

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtManager manager;
    private final CustomAuthenticationManager userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain) throws ServletException, IOException {
        final String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer")) {
            throw new AuthenticationCredentialsNotFoundException("JWT Token is missing");
        }

        try {
            String subject = manager.getSubject(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            if (manager.isValid(token)) {
                UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(upat);
            }

            Long companyId = manager.getId(token);
            request.setAttribute("companyId", companyId);

        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT Token is invalid");
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/auth"),
                new AntPathRequestMatcher("/company/**", "POST"),
                new AntPathRequestMatcher("/company/**", "HEAD")
        ).matches(request);
    }
}
