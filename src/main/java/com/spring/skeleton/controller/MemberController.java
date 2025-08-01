package com.spring.skeleton.controller;

import com.spring.skeleton.model.Member;
import com.spring.skeleton.model.MemberDetail;
import com.spring.skeleton.model.MembershipStatus;
import com.spring.skeleton.service.MemberService;
import com.spring.skeleton.common.OptNotBlank;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    public static class Body {
        @NotBlank(message = "이름은 필수입니다")
        private String name;
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "전화번호는 10자리 이상 15자리 이하의 숫자여야 합니다")
        private String phone;
        @Positive(message = "멤버십 아이디는 필수입니다")
        private Long membershipId;
        @FutureOrPresent(message = "시작일은 금일 이후여야 합니다")
        private Instant startDate;
        @OptNotBlank(message = "인증코드는 빈 값으로 생성할 수 없습니다.")
        private String verificationCode;
        private Boolean isRejoin;
    }

    @Data
    public static class SearchCriteria {
        private String name;
        private String phone;
        @Positive(message = "멤버십 아이디는 필수입니다")
        private Long membershipId;
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "시작일은 YYYY-MM-DD 형식이어야 합니다.")
        private String startDate;
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "종료일은 YYYY-MM-DD 형식이어야 합니다.")
        private String endDate;
        private String status;
        private String verificationCode;
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
                MembershipStatus.fromString(criteria.getStatus()),
                criteria.getVerificationCode()
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

    @GetMapping("/member/verification/{code}")
    public ResponseEntity<Member> verify(HttpServletRequest request,
                                         @PathVariable String code) {
        Long companyId = (Long) request.getAttribute("companyId");
        Member output = service.findByVerificationCode(code, companyId);
        return ResponseEntity.ok(output);
    }
}
