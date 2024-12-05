package com.spring.skeleton.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.skeleton.entity.MembershipEntity;
import com.spring.skeleton.entity.QMembershipEntity;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Membership;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MembershipService {

    Membership create(String name, Integer price, Integer duration);

    List<Membership> find(String name, Integer duration);

    Membership update(Long id,
                      String name,
                      Integer price,
                      Integer duration) throws EntityNotFoundException;
}

@Service
@RequiredArgsConstructor
class MembershipServiceImpl implements MembershipService {

    private final JPAQueryFactory factory;

    QMembershipEntity entity = QMembershipEntity.membershipEntity;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Membership create(String name, Integer price, Integer duration) {
        factory.insert(entity)
                .columns(entity.name, entity.price, entity.duration)
                .values(name, price, duration)
                .execute();
//TODO factory가 insert 후 id를 반환하거나 생성된 entity를 반환하는 메소드가 없어서 null을 반환하고 있습니다.
        return null;
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

        return list.stream().map(MembershipEntity::toModel).toList();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Membership update(Long id,
                             String name,
                             Integer price,
                             Integer duration) throws EntityNotFoundException {
//TODO id에 해당하는 entity가 없을 경우 EntityNotFoundException을 발생시켜야 합니다.
        factory.selectFrom(entity)
                .where(entity.id.eq(id))
                .fetchOne();

        factory.update(entity)
                .where(entity.id.eq(id))
                .set(entity.name, name)
                .set(entity.price, price)
                .set(entity.duration, duration)
                .execute();

        MembershipEntity updated = new MembershipEntity(id, name, price, duration);
        return MembershipEntity.toModel(updated);
    }
}
