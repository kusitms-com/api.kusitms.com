package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.user.MemberAuthority;
import com.kusitms.website.domain.user.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인/토큰 갱신 응답")
public class SignInResponse {
    @Schema(description = "Access Token (유효기간 1시간). 이후 요청에 Authorization: Bearer {accessToken} 헤더로 전달", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token (유효기간 7일). /api/auth/refresh로 새 토큰 발급 시 사용. 갱신할 때마다 새 값으로 교체됨", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "학회원 구분. 활동 기수 기준으로 로그인 시 자동 판별", implementation = String.class, example = "OB", allowableValues = {"YB", "OB"})
    private MemberRole role;

    @Schema(description = "어드민 권한. MANAGER 이상만 /admin API 사용 가능, OWNER만 권한 변경 가능", example = "MANAGER")
    private MemberAuthority authority;

    @Schema(description = "로그인 후 이동할 사이트 경로 (YB: /mypage, OB: /mypage/ob/profile)", example = "/mypage/ob/profile", allowableValues = {"/mypage", "/mypage/ob/profile"})
    private String redirectPath;
}
