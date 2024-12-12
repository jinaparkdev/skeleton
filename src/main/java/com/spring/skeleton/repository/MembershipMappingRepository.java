package com.spring.skeleton.repository;

import com.spring.skeleton.entity.MembershipMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipMappingRepository extends JpaRepository<MembershipMappingEntity, Long> {
}
