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

public interface MemberService {
    MemberDetail create(String name, String phone, Long membershipId, Instant startDate);

    List<Member> find(String name,
                      String phone,
                      Long membershipId,
                      Instant startDate,
                      Instant endDate,
                      MembershipStatus status);

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
class MemberServiceImpl implements MemberService {

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

        MembershipEntity membership = ensureExistingMembership(membershipId);
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
    public List<Member> find(String name,
                             String phone,
                             Long membershipId,
                             Instant startDate,
                             Instant endDate,
                             MembershipStatus status) {

        BooleanExpression matchesName =
                name != null ? mEntity.name.containsIgnoreCase(name) : mEntity.isNotNull();
        BooleanExpression matchesPhone =
                phone != null ? mEntity.phone.containsIgnoreCase(phone) : mEntity.isNotNull();
        BooleanExpression matchesMembershipId =
                membershipId != null ? mappingEntity.membership.id.eq(membershipId) : mappingEntity.isNotNull();
        BooleanExpression matchesStartDate =
                startDate != null ? mappingEntity.startDate.goe(startDate) : mappingEntity.isNotNull();
        BooleanExpression matchesEndDate =
                endDate != null ? mappingEntity.endDate.loe(endDate) : mappingEntity.isNotNull();
        BooleanExpression matchesStatus =
                status != null ? mappingEntity.status.eq(status.name()) : mappingEntity.isNotNull();

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

        MemberEntity member = ensureExistingMember(id);
        member.setName(name);
        member.setPhone(phoneNum);

        MembershipEntity membership = ensureExistingMembership(membershipId);
        MembershipMappingEntity mapping = ensureExistingMapping(id);
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

    private MembershipMappingEntity ensureExistingMapping(Long id) {
        return mappingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found"));
    }

    private MembershipEntity ensureExistingMembership(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found"));
    }

    private MemberEntity ensureExistingMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }
}
