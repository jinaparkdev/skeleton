package com.spring.skeleton.controller;

import com.spring.skeleton.model.Company;
import com.spring.skeleton.service.CompanyService;
import com.spring.skeleton.util.JwtManager;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;
    private final JwtManager jwtManager;

    @Data
    public static class Body {
        @NotBlank(message = "이름은 필수입니다")
        private String name;
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "전화번호는 10자리 이상 15자리 이하의 숫자여야 합니다")
        private String phone;
        @Email(message = "유효하지 않은 이메일입니다.")
        private String email;
        //TODO 비밀번호 정책 추가
        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @Data
    public static class ResetPasswordMailRequest {
        @Email(message = "유효하지 않은 이메일입니다.")
        private String email;
    }

    @Data
    public static class ResetPasswordRequest {
        //TODO 비밀번호 정책 추가
        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @PostMapping("/company")
    public ResponseEntity<Company> create(@RequestBody @Valid Body body) {

        Company output = service.create(
                body.getName(),
                body.getPhone(),
                body.getEmail(),
                body.getPassword()
                                       );

        return ResponseEntity.ok(output);
    }

    @RequestMapping(value = "/company/availability/phone/{phone}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> isAvailablePhone(@PathVariable String phone) {
        boolean available = service.isAvailablePhone(phone);
        return available ? ResponseEntity.ok().build() : ResponseEntity.status(409).build();
    }

    @RequestMapping(value = "/company/availability/email/{email}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> isAvailableEmail(@PathVariable String email) {
        boolean available = service.isAvailableEmail(email);
        return available ? ResponseEntity.ok().build() : ResponseEntity.status(409).build();
    }

    @PostMapping("/company/recovery/password")
    public ResponseEntity<Void> sendResetPasswordMail(@RequestBody @Valid ResetPasswordMailRequest request) {
        try {
            service.sendResetPasswordMail(request.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException("메일을 보내는 중 오류가 발생했습니다. 잠시후 다시 시도해주세요.", e);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/company/recovery/password/{token}")
    public ResponseEntity<Void> update(@PathVariable String token,
                                       @RequestBody @Valid ResetPasswordRequest request) {

        if (!jwtManager.isValid(token)) {
            return ResponseEntity.badRequest().build();
        }

        Long companyId = jwtManager.getId(token);
        service.resetPassword(companyId, request.getPassword());

        return ResponseEntity.ok().build();
    }
}
