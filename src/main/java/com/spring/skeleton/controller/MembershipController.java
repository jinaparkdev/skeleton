package com.spring.skeleton.controller;

import com.spring.skeleton.common.Validate;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Membership;
import com.spring.skeleton.service.MembershipService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    private final Validate validate = new Validate();

    @PostMapping("/membership")
    public ResponseEntity<Membership> create(@RequestBody CreateMembershipRequest request) {

        CreateMembershipRequest validated =
                validate.notNullOrEmpty(request.getName(), "Name")
                        .notNullOrEmpty(request.getPrice(), "Price")
                        .notNullOrEmpty(request.getDuration(), "Duration")
                        .confirm(request);

        Membership output = service.create(
                validated.getName(),
                validated.getPrice(),
                validated.getDuration()
                                          );
        return ResponseEntity.ok().body(output);
    }

    @GetMapping("/membership")
    public ResponseEntity<List<Membership>> find(@RequestParam Optional<String> name,
                                                 @RequestParam Optional<Integer> duration) {

        List<Membership> output = service.find(name, duration);
        return ResponseEntity.ok().body(output);
    }

    @PutMapping("/membership/{id}")
    public ResponseEntity<Membership> update(@PathVariable Long id,
                                             @RequestBody CreateMembershipRequest request) throws EntityNotFoundException {
        CreateMembershipRequest validated =
                validate.notNullOrEmpty(request.getName(), "Name")
                        .notNullOrEmpty(request.getPrice(), "Price")
                        .notNullOrEmpty(request.getDuration(), "Duration")
                        .confirm(request);

        Membership output = service.update(
                id,
                validated.getName(),
                validated.getPrice(),
                validated.getDuration()
                                          );

        return ResponseEntity.ok().body(output);
    }

    @DeleteMapping("/membership/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws EntityNotFoundException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
