package com.spring.skeleton.service;

import com.spring.skeleton.entity.CompanyEntity;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.AuthResponse;
import com.spring.skeleton.model.Authority;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.model.CustomUserDetails;
import com.spring.skeleton.repository.CompanyRepository;
import com.spring.skeleton.util.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationManager implements UserDetailsService {
    private final CompanyRepository companyRepository;
    private final JwtManager jwtManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CompanyEntity company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return new CustomUserDetails(
                company.getId(),
                company.getEmail(),
                company.getPassword(),
                List.of(Authority.Company)
        );
    }

    public AuthResponse authenticate(String email, String password) {
        CompanyEntity entity = companyRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));
        boolean matched =
                passwordEncoder.matches(password, entity.getPassword());

        if (!matched) {
            throw new EntityNotFoundException("Password does not match");
        }

        String token = jwtManager.generateToken(entity.getEmail(), entity.getId());

        return new AuthResponse(token, new Company(entity));
    }
}
