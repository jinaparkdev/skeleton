package com.spring.skeleton.service;

import com.spring.skeleton.entity.MembershipEntity;
import com.spring.skeleton.model.Membership;
import com.spring.skeleton.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface MembershipService {

    Membership create(String name, Integer price, Integer duration);

}

@Service
@RequiredArgsConstructor
class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository repository;

    @Override
    public Membership create(String name, Integer price, Integer duration) {
        MembershipEntity entity =
                repository.save(new MembershipEntity(null, name, price, duration));

        return MembershipEntity.toModel(entity);
    }
}
