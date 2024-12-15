package com.spring.skeleton.controller;

import com.spring.skeleton.util.Converter;
import com.spring.skeleton.util.Validator;
import com.spring.skeleton.model.Member;
import com.spring.skeleton.model.MemberDetail;
import com.spring.skeleton.model.MembershipStatus;
import com.spring.skeleton.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController extends Validator {

    private final MemberService service;

    @Getter
    @Setter
    public static class Request {
        private String name;
        private String phone;
        private Long membershipId;
        private String startDate;
        private String isRejoin;
    }

    @PostMapping("/member")
    public ResponseEntity<MemberDetail> create(@RequestBody Request request) {

        Request validated = notNullOrEmpty(request.getName(), "Name")
                .ensurePhoneNumber(request.getPhone())
                .notNullOrEmpty(request.getMembershipId(), "Membership ID")
                .ensureDate(request.getStartDate())
                .confirm(request);

        MemberDetail output = service.create(
                validated.getName(),
                validated.getPhone(),
                validated.getMembershipId(),
                Converter.toInstant(validated.getStartDate())
                                            );
        return ResponseEntity.ok(output);
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> find(@RequestParam Optional<String> name,
                                             @RequestParam Optional<String> phone,
                                             @RequestParam Optional<Long> membershipId,
                                             @RequestParam Optional<String> startDate,
                                             @RequestParam Optional<String> endDate,
                                             @RequestParam Optional<String> status) {

        Optional<Instant> startDt = startDate.map(this::ensureAndGetDate);
        Optional<Instant> endDt = endDate.map(this::ensureAndGetDate);
        Optional<MembershipStatus> membershipStatus = status.map(MembershipStatus::fromString);

        List<Member> output =
                service.find(name, phone, membershipId, startDt, endDt, membershipStatus);

        return ResponseEntity.ok(output);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<MemberDetail> findById(@PathVariable Long id) {
        MemberDetail output = service.findById(id);
        return ResponseEntity.ok(output);
    }

    @PutMapping("/member/{id}")
    public ResponseEntity<MemberDetail> update(@PathVariable Long id,
                                               @RequestBody Request request) {
        Request validated = notNullOrEmpty(request.getName(), "Name")
                .ensurePhoneNumber(request.getPhone())
                .notNullOrEmpty(request.getMembershipId(), "Membership ID")
                .ensureDate(request.getStartDate())
                .ensureBoolean(request.getIsRejoin())
                .confirm(request);

        MemberDetail output = service.update(
                id,
                validated.getName(),
                validated.getPhone(),
                validated.getMembershipId(),
                Converter.toInstant(validated.getStartDate()),
                Boolean.valueOf(request.getIsRejoin())
                                            );
        return ResponseEntity.ok(output);
    }
}
