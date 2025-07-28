package com.spring.skeleton.controller;

import com.spring.skeleton.model.AuthResponse;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.service.CompanyService;
import com.spring.skeleton.service.CustomAuthenticationManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;
    private final CustomAuthenticationManager authManager;

    @Data
    public static class Request {
        @NotBlank(message = "Name cannot be blank")
        private String name;
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        private String phone;
        @Email(message = "Invalid email format")
        private String email;
        //TODO 비밀번호 정책 추가
        @NotBlank(message = "Password cannot be blank")
        private String password;
    }

    @PostMapping("/company")
    public ResponseEntity<Company> create(@RequestBody @Valid Request request) {

        Company output = service.create(
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                request.getPassword()
                                       );

        return ResponseEntity.ok(output);
    }

    @PostMapping("/company/auth")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody Request request) {

        AuthResponse output =
                authManager.authenticate(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(output);
    }
}
