package com.spring.skeleton.service;

import com.spring.skeleton.entity.CompanyEntity;
import com.spring.skeleton.exception.AlreadyExistException;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public Company create(String name, String phone, String email, String password) {
        password = passwordEncoder.encode(password);

        if (!ensureAvailable(phone, email)) {
            throw new AlreadyExistException("이미 사용중인 전화번호 또는 이메일입니다.");
        }

        CompanyEntity entity = new CompanyEntity(name, phone, email, password);
        companyRepository.save(entity);
        return new Company(entity);
    }

    public boolean isAvailablePhone(String phone) {
        return companyRepository.findByPhone(phone).isEmpty();
    }

    public boolean isAvailableEmail(String email) {
        return companyRepository.findByEmail(email).isEmpty();
    }

    private boolean ensureAvailable(String phone, String email) {
        return companyRepository.findByPhoneOrEmail(phone, email).isEmpty();
    }
}
