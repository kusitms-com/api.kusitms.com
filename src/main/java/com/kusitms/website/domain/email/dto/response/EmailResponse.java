package com.kusitms.website.domain.email.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "모집 알림 이메일 등록 응답")
public class EmailResponse {
    @Schema(description = "이메일 ID", example = "12")
    private String id;

    @Schema(description = "저장된 이메일 (소문자로 정규화됨)", example = "kusitms@example.com")
    private String email;
}
