package com.kusitms.website.domain.email.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 컬럼 추가 이전에 수집된 이메일은 null이다.
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
