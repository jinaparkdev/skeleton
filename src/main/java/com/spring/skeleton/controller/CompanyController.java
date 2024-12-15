package com.spring.skeleton.controller;

import com.spring.skeleton.model.AuthResponse;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.service.CustomAuthenticationManager;
import com.spring.skeleton.service.CompanyService;
import com.spring.skeleton.util.Validator;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanyController extends Validator {

    private final CompanyService service;
    private final CustomAuthenticationManager authManager;

    @Getter
    @Setter
    public static class Request {
        private String name;
        private String phone;
        private String email;
        private String password;
    }

    @PostMapping("/company")
    public ResponseEntity<Company> create(@RequestBody Request request) {
        Request validRequest = notNullOrEmpty(request.getName(), "name")
                .ensureEmail(request.getEmail())
                .ensurePhoneNumber(request.getPhone())
                .notNullOrEmpty(request.getPassword(), "password")
                .confirm(request);

        Company output = service.create(
                validRequest.getName(),
                validRequest.getPhone(),
                validRequest.getEmail(),
                validRequest.getPassword()
                                       );

        return ResponseEntity.ok(output);
    }

    @PostMapping("/company/auth")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody Request request) {
        Request validRequest = ensureEmail(request.getEmail())
                .notNullOrEmpty(request.getPassword(), "password")
                .confirm(request);

        AuthResponse output =
                authManager.authenticate(validRequest.getEmail(), validRequest.getPassword());

        return ResponseEntity.ok(output);
    }
}
