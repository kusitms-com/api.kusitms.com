package com.kusitms.website.domain.email.service;

import com.kusitms.website.domain.email.dto.response.AdminEmailResponse;
import com.kusitms.website.domain.email.entity.Email;
import com.kusitms.website.domain.email.dto.response.EmailResponse;
import com.kusitms.website.domain.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final String DUPLICATE_MESSAGE = "이미 등록된 이메일입니다.";
    // Excel에서 UTF-8 CSV를 올바르게 열도록 BOM을 붙인다.
    private static final String UTF8_BOM = "﻿";

    @Transactional
    public EmailResponse saveEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }

        String normalized = email.trim().toLowerCase(Locale.ROOT);

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        if (emailRepository.existsByEmailIgnoreCase(normalized)) {
            throw new IllegalArgumentException(DUPLICATE_MESSAGE);
        }

        Email savedEmail;
        try {
            savedEmail = emailRepository.saveAndFlush(
                    Email.builder()
                            .email(normalized)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            // 같은 이메일이 동시에 등록되면 unique 제약에서 걸리므로 중복으로 응답한다.
            throw new IllegalArgumentException(DUPLICATE_MESSAGE);
        }

        return EmailResponse.builder()
                .id(String.valueOf(savedEmail.getId()))
                .email(savedEmail.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdminEmailResponse> getEmails() {
        return emailRepository.findAllByOrderByIdDesc().stream()
                .map(AdminEmailResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] exportEmailsAsCsv() {
        StringBuilder csv = new StringBuilder(UTF8_BOM).append("email,created_at\n");
        for (Email email : emailRepository.findAllByOrderByIdDesc()) {
            csv.append(escapeCsvCell(email.getEmail())).append(',')
                    .append(email.getCreatedAt() != null ? email.getCreatedAt().toString() : "")
                    .append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public void deleteEmail(Long emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));
        emailRepository.delete(email);
    }

    // 스프레드시트에서 수식으로 해석될 수 있는 값(=, +, -, @로 시작)은 문자열로 취급되게 한다.
    private String escapeCsvCell(String value) {
        if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) {
            return "'" + value;
        }
        return value;
    }
}
