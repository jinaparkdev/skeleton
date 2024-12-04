package com.spring.skeleton.repository;

import com.spring.skeleton.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity, Long> {

    MembershipEntity save(MembershipEntity entity);
}
