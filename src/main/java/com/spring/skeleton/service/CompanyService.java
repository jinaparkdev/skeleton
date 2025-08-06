package com.spring.skeleton.service;

import com.spring.skeleton.entity.CompanyEntity;
import com.spring.skeleton.exception.AlreadyExistException;
import com.spring.skeleton.exception.EntityNotFoundException;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.repository.CompanyRepository;
import com.spring.skeleton.util.JwtManager;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;
    private final JwtManager tokenManager;

    @Value("${app.domain}")
    private String domain;

    public Company create(String name, String phone, String email, String password) {
        password = passwordEncoder.encode(password);

        if (!ensureAvailable(phone, email)) {
            throw new AlreadyExistException("이미 사용중인 전화번호 또는 이메일입니다.");
        }

        CompanyEntity entity = new CompanyEntity(name, phone, email, password);
        companyRepository.save(entity);
        return new Company(entity);
    }

    public void sendResetPasswordMail(String email) throws MessagingException {

        CompanyEntity company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일로 가입된 계정이 없습니다."));

        String token = tokenManager.generate(
                company.getEmail(),
                company.getId(),
                30L * 60 * 1000// 30분 유효한 토큰 생성
                                            );

        String resetLink = domain + "/recovery/password/" + token;
        String subject = "[Hub-T] 비밀번호 재설정";
        String htmlContent = """
                    <div style=\"background:#FFD740;padding:32px 0;text-align:center;font-family:sans-serif;\">
                        <div style=\"background:#fff;border-radius:8px;max-width:400px;margin:0 auto;padding:32px;box-shadow:0 2px 8px rgba(0,0,0,0.08);\">
                            <h2 style=\"color:#333;margin-bottom:16px;\">비밀번호 재설정 안내</h2>
                            <p style=\"color:#555;font-size:16px;line-height:1.6;\">비밀번호를 재설정하려면 아래 버튼을 클릭하세요.</p>
                            <p style=\"color:#FF5722;font-size:14px;margin-top:12px;font-weight:bold;\">⏰ 이 링크는 30분 동안 유효합니다.</p>
                            <a href=\"%s\" style=\"display:inline-block;margin-top:24px;padding:12px 32px;background:#FFD740;color:#222;text-decoration:none;font-weight:bold;border-radius:4px;font-size:16px;box-shadow:0 1px 4px rgba(0,0,0,0.06);transition:background 0.2s;\">비밀번호 재설정</a>
                        </div>
                        <p style=\"margin-top:24px;color:#888;font-size:13px;\">본 메일은 Hub-T 서비스에서 발송되었습니다.</p>
                    </div>
                """.formatted(resetLink);

        mailSender.send(email, subject, htmlContent);
    }

    public boolean isAvailablePhone(String phone) {
        return companyRepository.findByPhone(phone).isEmpty();
    }

    public boolean isAvailableEmail(String email) {
        return companyRepository.findByEmail(email).isEmpty();
    }

    public void resetPassword(Long companyId, String password) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("회사를 찾을 수 없습니다."));
        company.setPassword(passwordEncoder.encode(password));
        companyRepository.save(company);
    }

    private boolean ensureAvailable(String phone, String email) {
        return companyRepository.findByPhoneOrEmail(phone, email).isEmpty();
    }
}
