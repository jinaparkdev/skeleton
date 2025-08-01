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
import java.util.stream.Stream;

public interface MemberService {
    MemberDetail create(String name,
                        String phone,
                        Long membershipId,
                        Instant startDate,
                        String verificationCode);

    List<Member> find(String name,
                      String phone,
                      Long membershipId,
                      Instant startDate,
                      Instant endDate,
                      MembershipStatus status,
                      String verificationCode);

    MemberDetail findById(Long id) throws EntityNotFoundException;

    MemberDetail update(Long id,
                        String name,
                        String phone,
                        Long membershipId,
                        Instant startDate,
                        Boolean isRejoin) throws EntityNotFoundException;

    Member findByVerificationCode(String verificationCode,
                                  Long companyId) throws EntityNotFoundException;
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
    public MemberDetail create(String name,
                               String phone,
                               Long membershipId,
                               Instant startDate,
                               String verificationCode) {

        String phoneNum = ensureAvailablePhone(phone, null);
        String verificationCodeNum =
                ensureAvailableVerificationCode(verificationCode, membershipId);

        MembershipEntity membership = resolve(membershipRepository, membershipId);
        MemberEntity member = new MemberEntity(name, phoneNum);
        memberRepository.save(member);

        MembershipMappingEntity mapping = new MembershipMappingEntity(
                member,
                membership,
                startDate,
                MembershipStatus.New.name(),
                verificationCodeNum
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
                             MembershipStatus status,
                             String verificationCode) {

        BooleanExpression matchesName =
                name != null ? mEntity.name.containsIgnoreCase(name) : mEntity.isNotNull();
        BooleanExpression matchesPhone =
                phone != null ? mEntity.phone.containsIgnoreCase(phone) : mEntity.isNotNull();
        BooleanExpression matchesMembershipId =
                membershipId != null ? mappingEntity.membership.id.eq(membershipId) : mappingEntity.isNotNull();
        BooleanExpression matchesStartDate =
                startDate != null ? mappingEntity.startDate.goe(startDate) : mappingEntity.isNotNull();
        BooleanExpression matchesEndDate =
                endDate != null ? mappingEntity.startDate.loe(endDate) : mappingEntity.isNotNull();
        BooleanExpression matchesStatus =
                status != null ? mappingEntity.status.eq(status.name()) : mappingEntity.isNotNull();
        BooleanExpression matchesVerificationCode =
                verificationCode != null ? mappingEntity.verificationCode.eq(verificationCode) : mappingEntity.isNotNull();

        Predicate condition =
                Stream.of(
                        matchesName,
                        matchesPhone,
                        matchesMembershipId,
                        matchesStartDate,
                        matchesEndDate,
                        matchesStatus,
                        matchesVerificationCode
                         ).reduce(BooleanExpression::and).orElseThrow();

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

        if (isRejoin != null && isRejoin) {
            mapping.setStatus(MembershipStatus.Rejoined.name());
        }

        return new MemberDetail(mapping);
    }

    @Override
    public Member findByVerificationCode(String verificationCode,
                                         Long companyId) throws EntityNotFoundException {

        Optional<MembershipMappingEntity> entity =
                mappingRepository.findByVerificationCode(verificationCode)
                        .stream()
                        .filter(m -> m.getMembership().getCompany().getId().equals(companyId))
                        .findFirst();

        return entity.map(Member::new)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "코드를 확인해주세요.: " + verificationCode));
    }

    private String ensureAvailablePhone(String phone, Long id) {
        memberRepository.findByPhone(phone).ifPresent(
                member -> {
                    if (!member.getId().equals(id))
                        throw new AlreadyExistException("Phone number already exists");
                });
        return phone;
    }

    private String ensureAvailableVerificationCode(String code, Long membershipId) {
        Boolean available =
                mappingRepository.findByVerificationCodeAndMembershipId(code, membershipId)
                        .isEmpty();

        if (!available) {
            throw new AlreadyExistException("이미 존재하는 인증 코드입니다: " + code);
        }

        return code;
    }
}
