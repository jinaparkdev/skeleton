package com.spring.skeleton.controller;

import com.spring.skeleton.model.Member;
import com.spring.skeleton.model.MemberDetail;
import com.spring.skeleton.model.MembershipStatus;
import com.spring.skeleton.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static com.spring.skeleton.util.Converter.toInstant;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @Data
    public static class Request {
        @NotBlank(message = "Name cannot be null or empty")
        private String name;
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        private String phone;
        @Positive(message = "Membership ID must be positive")
        private Long membershipId;
        @FutureOrPresent(message = "Start date must be in the present or future")
        private Instant startDate;
        private Boolean isRejoin;
    }

    @Data
    public static class SearchRequest {
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
    public ResponseEntity<MemberDetail> create(@RequestBody @Valid Request request) {

        MemberDetail output = service.create(
                request.getName(),
                request.getPhone(),
                request.getMembershipId(),
                request.getStartDate()
                                            );

        return ResponseEntity.ok(output);
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> find(@Valid SearchRequest request) {

        List<Member> output = service.find(
                request.getName(),
                request.getPhone(),
                request.getMembershipId(),
                toInstant(request.getStartDate()),
                toInstant(request.getEndDate()),
                MembershipStatus.fromString(request.getStatus())
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
                                               @RequestBody @Valid Request request) {

        MemberDetail output = service.update(
                id,
                request.getName(),
                request.getPhone(),
                request.getMembershipId(),
                request.getStartDate(),
                request.getIsRejoin()
                                            );
        return ResponseEntity.ok(output);
    }
}
