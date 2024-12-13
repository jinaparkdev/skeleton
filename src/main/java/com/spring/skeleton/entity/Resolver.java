package com.spring.skeleton.entity;

import com.spring.skeleton.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class Resolver {
    public <T extends JpaRepository<E, ID>, E, ID> E resolve(T repository, ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }
}
