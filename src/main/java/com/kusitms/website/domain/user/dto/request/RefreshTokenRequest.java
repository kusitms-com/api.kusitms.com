package com.kusitms.website.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "토큰 갱신 요청")
public class RefreshTokenRequest {
    @Schema(description = "로그인/토큰 갱신 응답으로 받은 Refresh Token", example = "eyJhbGciOiJIUzUxMiJ9...", required = true)
    private String refreshToken;
}
