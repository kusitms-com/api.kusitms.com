package com.kusitms.website.domain.user.dto.response;

import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.Part;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 계정 정보 조회 응답")
public class AccountProfileResponse {

    @Schema(description = "계정 프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "전화번호")
    private String phone;

    @Schema(description = "기수")
    private Integer cardinal;

    @Schema(description = "아이디")
    private String id;

    @Schema(description = "파트")
    private Part part;

    public static AccountProfileResponse from(Member member) {
        return AccountProfileResponse.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .name(member.getName())
                .phone(member.getPhone())
                .cardinal(member.getCardinal())
                .id(member.getId())
                .part(member.getPart())
                .build();
    }
}
