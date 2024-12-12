package com.spring.skeleton.controller;

import com.spring.skeleton.common.Converter;
import com.spring.skeleton.common.Validate;
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

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @Getter
    @Setter
    public static class CreateMemberRequest {
        private String name;
        private String phone;
        private Long membershipId;
        private String startDate;
    }

    private final Validate validate = new Validate();

    @PostMapping("/member")
    public ResponseEntity<MemberDetail> create(@RequestBody CreateMemberRequest request) {

        CreateMemberRequest validated =
                validate.notNullOrEmpty(request.getName(), "Name")
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
    public ResponseEntity<List<Member>> find(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String phone,
                                             @RequestParam(required = false) Long membershipId,
                                             @RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate,
                                             @RequestParam(required = false) String status) {

        Instant startDt = startDate != null ?
                validate.ensureDate(startDate).confirm(Converter.toInstant(startDate)) : null;
        Instant endDt = endDate != null ?
                validate.ensureDate(endDate).confirm(Converter.toInstant(endDate)) : null;
        MembershipStatus membershipStatus =
                status != null ? MembershipStatus.fromString(status) : null;

        List<Member> output =
                service.find(name, phone, membershipId, startDt, endDt, membershipStatus);

        return ResponseEntity.ok(output);
    }
}
