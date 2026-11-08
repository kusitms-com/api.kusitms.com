package com.kusitms.website.domain.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모집 알림 이메일 등록 요청")
public class EmailSaveRequest {
    @Schema(description = "모집 소식을 받을 이메일. 앞뒤 공백을 제거하고 소문자로 저장합니다.", example = "kusitms@example.com", required = true)
    private String email;
}
