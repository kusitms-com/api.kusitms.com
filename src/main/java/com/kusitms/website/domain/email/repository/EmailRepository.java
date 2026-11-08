package com.kusitms.website.domain.email.repository;

import com.kusitms.website.domain.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    // 정규화 이전에 저장된 대소문자 혼합 이메일과도 중복을 판별한다.
    boolean existsByEmailIgnoreCase(String email);
}
