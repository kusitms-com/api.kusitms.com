package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.user.entity.MemberAuthority;
import com.kusitms.website.domain.user.entity.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답")
public class SignInResponse {
    @Schema(description = "Access Token")
    private String accessToken;

    @Schema(description = "Refresh Token")
    private String refreshToken;

    @Schema(description = "사용자 역할 (YB/OB)")
    private MemberRole role;

    @Schema(description = "어드민 권한 (MEMBER/MANAGER/OWNER). MANAGER 이상만 어드민 API 사용 가능")
    private MemberAuthority authority;

    @Schema(description = "리다이렉트 경로")
    private String redirectPath;
}
