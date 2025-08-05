package com.spring.skeleton.repository;

import com.spring.skeleton.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByEmail(String email);

    Optional<CompanyEntity> findByPhoneOrEmail(String phone, String email);

    Collection<Object> findByPhone(String phone);
}
