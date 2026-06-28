package com.kusitms.website.domain.admin.dto.response;

import com.kusitms.website.domain.user.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "승인 대기 회원 응답")
public class PendingMemberResponse {
    @Schema(description = "회원 PK")
    private Long userId;

    @Schema(description = "아이디")
    private String id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "휴대폰 번호")
    private String phone;

    @Schema(description = "활동 기수")
    private Integer cardinal;

    @Schema(description = "파트")
    private String part;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "수료증 이미지 URL")
    private String certificateImageUrl;

    public static PendingMemberResponse from(Member member) {
        return new PendingMemberResponse(
                member.getUserId(),
                member.getId(),
                member.getName(),
                member.getPhone(),
                member.getCardinal(),
                member.getPart().name(),
                member.getProfileImageUrl(),
                member.getCertificateImageUrl()
        );
    }
}
