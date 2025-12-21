package com.kusitms.website.domain.email.service;

import com.kusitms.website.domain.email.Email;
import com.kusitms.website.domain.email.dto.response.EmailResponse;
import com.kusitms.website.domain.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Transactional
    public EmailResponse saveEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        if (emailRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        Email savedEmail = emailRepository.save(
                Email.builder()
                        .email(email)
                        .build()
        );

        return EmailResponse.builder()
                .id(String.valueOf(savedEmail.getId()))
                .email(savedEmail.getEmail())
                .build();
    }
}
