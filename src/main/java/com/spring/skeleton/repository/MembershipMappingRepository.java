package com.spring.skeleton.repository;

import com.spring.skeleton.entity.MembershipMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipMappingRepository extends JpaRepository<MembershipMappingEntity, Long> {
    List<MembershipMappingEntity> findByVerificationCodeAndMembershipId(String verificationCode, Long membershipId);

    List<MembershipMappingEntity> findByVerificationCode(String verificationCode);
}
