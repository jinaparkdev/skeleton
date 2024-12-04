package com.spring.skeleton.controller;

import com.spring.skeleton.model.Membership;
import com.spring.skeleton.service.MembershipService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService service;

    @PostMapping("/membership")
    public ResponseEntity<Membership> create(@RequestBody CreateMembershipRequest request) {

        System.out.println("request= " + request.toString());
        return null;
    }

    @Getter
    @Setter
    @ToString
    private static class CreateMembershipRequest {
        private String name;
        private Integer price;
        private Integer duration;
    }
}
