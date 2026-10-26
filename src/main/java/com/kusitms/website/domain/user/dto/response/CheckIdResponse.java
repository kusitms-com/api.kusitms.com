package com.kusitms.website.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "아이디 중복 체크 응답")
public class CheckIdResponse {
    @Schema(description = "사용 가능 여부", example = "true")
    private boolean available;

    @Schema(description = "안내 메시지", example = "사용 가능한 아이디입니다.", allowableValues = {"사용 가능한 아이디입니다.", "이미 사용 중인 아이디입니다."})
    private String message;
}
