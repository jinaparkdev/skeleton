package com.spring.skeleton.controller;

import com.spring.skeleton.util.JwtManager;
import com.spring.skeleton.util.Validator;
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
public class MembershipController extends Validator {

    private final MembershipService service;
    private final JwtManager jwtManager;

    @Getter
    @Setter
    public static class Request {
        private String name;
        private Integer price;
        private Integer duration;
    }

    @PostMapping("/membership")
    public ResponseEntity<Membership> create(@RequestHeader("Authorization") String token,
                                             @RequestBody Request request) {

        Request validated = notNullOrEmpty(request.getName(), "Name")
                .notNullOrEmpty(request.getPrice(), "Price")
                .notNullOrEmpty(request.getDuration(), "Duration")
                .confirm(request);

        Long companyId = jwtManager.getIdFromToken(token);

        Membership output = service.create(
                validated.getName(),
                validated.getPrice(),
                validated.getDuration(),
                companyId
                                          );
        return ResponseEntity.ok().body(output);
    }

    @GetMapping("/membership")
    public ResponseEntity<List<Membership>> find(@RequestHeader("Authorization") String token,
                                                 @RequestParam Optional<String> name,
                                                 @RequestParam Optional<Integer> duration) {

        Long companyId = jwtManager.getIdFromToken(token);
        List<Membership> output = service.find(name, duration, companyId);
        return ResponseEntity.ok().body(output);
    }

    @PutMapping("/membership/{id}")
    public ResponseEntity<Membership> update(@PathVariable Long id,
                                             @RequestBody Request request) throws EntityNotFoundException {
        Request validated = notNullOrEmpty(request.getName(), "Name")
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
