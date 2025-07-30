package com.spring.skeleton.controller;

import com.spring.skeleton.model.Member;
import com.spring.skeleton.model.MemberDetail;
import com.spring.skeleton.model.MembershipStatus;
import com.spring.skeleton.service.MemberService;
import com.spring.skeleton.util.OptNotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.spring.skeleton.util.Converter.toInstant;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @Data
    public static class Body {
        @NotBlank(message = "Name cannot be empty")
        private String name;
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        private String phone;
        @Positive(message = "Membership ID must be positive")
        private Long membershipId;
        @FutureOrPresent(message = "Start date must be in the present or future")
        private Instant startDate;
        @OptNotBlank(message = "Verification code cannot be empty")
        private String verificationCode;
        private Boolean isRejoin;
    }

    @Data
    public static class SearchCriteria {
        private String name;
        private String phone;
        @Positive(message = "Membership ID must be positive")
        private Long membershipId;
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in YYYY-MM-DD format")
        private String startDate;
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in YYYY-MM-DD format")
        private String endDate;
        private String status;
    }

    @PostMapping("/member")
    public ResponseEntity<MemberDetail> create(@RequestBody @Valid Body body) {
        MemberDetail output = service.create(
                body.getName(),
                body.getPhone(),
                body.getMembershipId(),
                body.getStartDate(),
                body.getVerificationCode()
                                            );

        return ResponseEntity.ok(output);
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> find(@Valid SearchCriteria criteria) {

        List<Member> output = service.find(
                criteria.getName(),
                criteria.getPhone(),
                criteria.getMembershipId(),
                toInstant(criteria.getStartDate()),
                toInstant(criteria.getEndDate()),
                MembershipStatus.fromString(criteria.getStatus())
                                          );

        return ResponseEntity.ok(output);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<MemberDetail> findById(@PathVariable Long id) {
        MemberDetail output = service.findById(id);
        return ResponseEntity.ok(output);
    }

    @PutMapping("/member/{id}")
    public ResponseEntity<MemberDetail> update(@PathVariable Long id,
                                               @RequestBody @Valid Body body) {

        MemberDetail output = service.update(
                id,
                body.getName(),
                body.getPhone(),
                body.getMembershipId(),
                body.getStartDate(),
                body.getIsRejoin()
                                            );
        return ResponseEntity.ok(output);
    }
}
