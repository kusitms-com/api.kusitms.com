package com.kusitms.website.domain.email.dto.response;

import com.kusitms.website.domain.email.entity.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "어드민 모집 알림 이메일 응답")
public class AdminEmailResponse {
    @Schema(description = "이메일 ID", example = "12")
    private Long id;

    @Schema(description = "이메일", example = "kusitms@example.com")
    private String email;

    @Schema(description = "수집 일시. 수집 일시 기록 이전에 등록된 이메일은 null", example = "2026-11-08T14:30:00", nullable = true)
    private LocalDateTime createdAt;

    public static AdminEmailResponse from(Email email) {
        return new AdminEmailResponse(email.getId(), email.getEmail(), email.getCreatedAt());
    }
}
