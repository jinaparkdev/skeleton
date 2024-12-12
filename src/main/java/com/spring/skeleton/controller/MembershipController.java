package com.spring.skeleton.controller;

import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Membership;
import com.spring.skeleton.service.MembershipService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService service;

    @Getter
    @Setter
    public static class CreateMembershipRequest {
        private String name;
        private Integer price;
        private Integer duration;
    }

    @PostMapping("/membership")
    public ResponseEntity<Membership> create(@RequestBody CreateMembershipRequest request) {

        Membership output = service.create(
                request.getName(),
                request.getPrice(),
                request.getDuration()
                                          );
        return ResponseEntity.ok().body(output);
    }

    @GetMapping("/membership")
    public ResponseEntity<List<Membership>> find(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) Integer duration) {

        List<Membership> output = service.find(name, duration);
        return ResponseEntity.ok().body(output);
    }

    @PutMapping("/membership/{id}")
    public ResponseEntity<Membership> update(@PathVariable Long id,
                                             @RequestBody CreateMembershipRequest request) throws EntityNotFoundException {

        Membership output = service.update(
                id,
                request.getName(),
                request.getPrice(),
                request.getDuration()
                                          );

        return ResponseEntity.ok().body(output);
    }

    @DeleteMapping("/membership/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws EntityNotFoundException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
