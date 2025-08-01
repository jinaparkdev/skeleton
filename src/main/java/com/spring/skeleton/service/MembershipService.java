package com.spring.skeleton.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.skeleton.entity.CompanyEntity;
import com.spring.skeleton.entity.MembershipEntity;
import com.spring.skeleton.entity.QMembershipEntity;
import com.spring.skeleton.entity.Resolver;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Membership;
import com.spring.skeleton.repository.CompanyRepository;
import com.spring.skeleton.repository.MembershipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface MembershipService {

    Membership create(String name, Integer price, Integer duration, Long companyId);

    List<Membership> find(Optional<String> name, Optional<Integer> duration, Long companyId);

    Membership update(Long id,
                      String name,
                      Integer price,
                      Integer duration) throws EntityNotFoundException;

    void delete(Long id);
}

@Service
@RequiredArgsConstructor
class MembershipServiceImpl extends Resolver implements MembershipService {

    private final MembershipRepository repository;
    private final CompanyRepository companyRepository;
    private final JPAQueryFactory factory;

    private final QMembershipEntity entity = QMembershipEntity.membershipEntity;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Membership create(String name, Integer price, Integer duration, Long companyId) {
        CompanyEntity company = resolve(companyRepository, companyId);
        MembershipEntity m = repository.save(new MembershipEntity(name, price, duration, company));
        return new Membership(m);
    }

    @Override
    public List<Membership> find(Optional<String> name,
                                 Optional<Integer> duration,
                                 Long companyId) {

        BooleanExpression matchesName =
                name.map(entity.name::eq).orElseGet(entity::isNotNull);
        BooleanExpression matchesDuration =
                duration.map(entity.duration::eq).orElseGet(entity::isNotNull);
        BooleanExpression matchesCompany = entity.company.id.eq(companyId);

        Predicate condition = matchesName.and(matchesDuration).and(matchesCompany);

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
        MembershipEntity m = resolve(repository, id);

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
            throw new EntityNotFoundException("멤버십을 찾을 수 없습니다: " + id);
        }
    }
}
