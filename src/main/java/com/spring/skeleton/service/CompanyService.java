package com.spring.skeleton.service;

import com.spring.skeleton.entity.CompanyEntity;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

public interface CompanyService {
    Company create(String name, String phone, String email, String password);
}

@Service
@RequiredArgsConstructor
class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Company create(String name, String phone, String email, String password) {
        password = passwordEncoder.encode(password);
        CompanyEntity entity = new CompanyEntity(name, phone, email, password);
        companyRepository.save(entity);
        return new Company(entity);
    }
}
