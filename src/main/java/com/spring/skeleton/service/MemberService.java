package com.spring.skeleton.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.skeleton.entity.*;
import com.spring.skeleton.exception.AlreadyExistException;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Member;
import com.spring.skeleton.model.MemberDetail;
import com.spring.skeleton.model.MembershipStatus;
import com.spring.skeleton.repository.MemberRepository;
import com.spring.skeleton.repository.MembershipMappingRepository;
import com.spring.skeleton.repository.MembershipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    MemberDetail create(String name, String phone, Long membershipId, Instant startDate);

    List<Member> find(Optional<String> name,
                      Optional<String> phone,
                      Optional<Long> membershipId,
                      Optional<Instant> startDate,
                      Optional<Instant> endDate,
                      Optional<MembershipStatus> status);

    MemberDetail findById(Long id) throws EntityNotFoundException;

    MemberDetail update(Long id,
                        String name,
                        String phone,
                        Long membershipId,
                        Instant startDate,
                        Boolean isRejoin) throws EntityNotFoundException;
}

@Service
@RequiredArgsConstructor
class MemberServiceImpl extends Resolver implements MemberService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipMappingRepository mappingRepository;
    private final JPAQueryFactory factory;

    private final QMemberEntity mEntity = QMemberEntity.memberEntity;
    private final QMembershipMappingEntity mappingEntity =
            QMembershipMappingEntity.membershipMappingEntity;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MemberDetail create(String name, String phone, Long membershipId, Instant startDate) {

        String phoneNum = ensureAvailablePhone(phone, null);

        MembershipEntity membership = resolve(membershipRepository, membershipId);
        MemberEntity member = new MemberEntity(name, phoneNum);
        memberRepository.save(member);

        MembershipMappingEntity mapping = new MembershipMappingEntity(
                member,
                membership,
                startDate,
                MembershipStatus.New.name()
        );

        MembershipMappingEntity mappingEntity = mappingRepository.save(mapping);
        return new MemberDetail(mappingEntity);
    }

    @Override
    public List<Member> find(Optional<String> name,
                             Optional<String> phone,
                             Optional<Long> membershipId,
                             Optional<Instant> startDate,
                             Optional<Instant> endDate,
                             Optional<MembershipStatus> status) {

        BooleanExpression matchesName =
                name.map(mEntity.name::containsIgnoreCase).orElseGet(mEntity::isNotNull);
        BooleanExpression matchesPhone =
                phone.map(mEntity.phone::containsIgnoreCase).orElseGet(mEntity::isNotNull);
        BooleanExpression matchesMembershipId =
                membershipId.map(mappingEntity.id::eq).orElseGet(mappingEntity::isNotNull);
        BooleanExpression matchesStartDate =
                startDate.map(mappingEntity.startDate::goe).orElseGet(mappingEntity::isNotNull);
        BooleanExpression matchesEndDate =
                endDate.map(mappingEntity.startDate::loe).orElseGet(mappingEntity::isNotNull);
        BooleanExpression matchesStatus =
                status.map(s -> mappingEntity.status.eq(s.name()))
                        .orElseGet(mappingEntity::isNotNull);

        Predicate condition = matchesName
                .and(matchesPhone)
                .and(matchesMembershipId)
                .and(matchesStartDate)
                .and(matchesEndDate)
                .and(matchesStatus);

        List<MembershipMappingEntity> list =
                factory.selectFrom(mappingEntity)
                        .join(mappingEntity.member, mEntity)
                        .where(condition)
                        .fetch();

        return list.stream().map(Member::new).toList();
    }

    @Override
    public MemberDetail findById(Long id) throws EntityNotFoundException {
        MembershipMappingEntity mapping = mappingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        return new MemberDetail(mapping);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MemberDetail update(Long id,
                               String name,
                               String phone,
                               Long membershipId,
                               Instant startDate,
                               Boolean isRejoin) throws EntityNotFoundException {

        String phoneNum = ensureAvailablePhone(phone, id);

        MemberEntity member = resolve(memberRepository, id);
        member.setName(name);
        member.setPhone(phoneNum);

        MembershipEntity membership = resolve(membershipRepository, membershipId);
        MembershipMappingEntity mapping = resolve(mappingRepository, id);
        mapping.setMembership(membership);
        mapping.setStartDate(startDate);

        if (isRejoin) {
            mapping.setStatus(MembershipStatus.Rejoined.name());
        }

        return new MemberDetail(mapping);
    }

    private String ensureAvailablePhone(String phone, Long id) {
        memberRepository.findByPhone(phone).ifPresent(
                member -> {
                    if (!member.getId().equals(id))
                        throw new AlreadyExistException("Phone number already exists");
                });
        return phone;
    }
}
