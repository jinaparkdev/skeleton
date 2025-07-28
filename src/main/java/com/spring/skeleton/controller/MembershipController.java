package com.spring.skeleton.controller;

import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Membership;
import com.spring.skeleton.service.MembershipService;
import com.spring.skeleton.util.JwtManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService service;
    private final JwtManager jwtManager;

    @Data
    public static class Request {
        @NotBlank(message = "Name cannot be null or empty")
        private String name;
        @PositiveOrZero(message = "Price must be zero or positive")
        private Integer price;
        @Positive(message = "Duration must be positive")
        private Integer duration;
    }

    @PostMapping("/membership")
    public ResponseEntity<Membership> create(@RequestHeader("Authorization") String token,
                                             @RequestBody @Valid Request request) {

        Long companyId = jwtManager.getIdFromToken(token);

        Membership output = service.create(
                request.getName(),
                request.getPrice(),
                request.getDuration(),
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
                                             @RequestBody @Valid Request request) throws EntityNotFoundException {

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
