package com.spring.skeleton.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.skeleton.entity.MembershipEntity;
import com.spring.skeleton.entity.QMembershipEntity;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Membership;
import com.spring.skeleton.repository.MembershipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MembershipService {

    Membership create(String name, Integer price, Integer duration);

    List<Membership> find(String name, Integer duration);

    Membership update(Long id,
                      String name,
                      Integer price,
                      Integer duration) throws EntityNotFoundException;

    void delete(Long id);
}

@Service
@RequiredArgsConstructor
class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository repository;
    private final JPAQueryFactory factory;

    QMembershipEntity entity = QMembershipEntity.membershipEntity;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Membership create(String name, Integer price, Integer duration) {
        MembershipEntity m = repository.save(new MembershipEntity(name, price, duration));
        return new Membership(m);
    }

    @Override
    public List<Membership> find(String name, Integer duration) {

        BooleanExpression matchesName =
                name != null ? entity.name.containsIgnoreCase(name) : entity.isNotNull();

        BooleanExpression matchesDuration = duration != null ? entity.duration.eq(duration) : entity.isNotNull();

        Predicate condition = matchesName.and(matchesDuration);

        List<MembershipEntity> list = factory.selectFrom(entity)
                .where(condition)
                .fetch();

        return list.stream().map(Membership::new).toList();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Membership update(Long id,
                             String name,
                             Integer price,
                             Integer duration) throws EntityNotFoundException {
        MembershipEntity m = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Membership not found"));

        m.setName(name);
        m.setPrice(price);
        m.setDuration(duration);

        return new Membership(m);
    }

    @Override
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Membership not found");
        }
    }
}
