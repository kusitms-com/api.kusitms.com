package com.kusitms.website.domain.admin.dto.response;

import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberAuthority;
import com.kusitms.website.domain.user.MemberRole;
import com.kusitms.website.domain.user.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "어드민 회원 목록 응답")
public class AdminMemberResponse {
    @Schema(description = "회원 PK")
    private Long userId;

    @Schema(description = "아이디")
    private String id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "휴대폰 번호")
    private String phone;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "활동 기수")
    private Integer cardinal;

    @Schema(description = "파트")
    private String part;

    @Schema(description = "가입 상태 (PENDING/APPROVED/WITHDRAWN)")
    private MemberStatus status;

    @Schema(description = "학회원 구분 (YB/OB)")
    private MemberRole role;

    @Schema(description = "어드민 권한 (MEMBER/MANAGER/OWNER)")
    private MemberAuthority authority;

    @Schema(description = "가입 일시")
    private LocalDateTime createdAt;

    public static AdminMemberResponse from(Member member) {
        return new AdminMemberResponse(
                member.getUserId(),
                member.getId(),
                member.getName(),
                member.getPhone(),
                member.getEmail(),
                member.getCardinal(),
                member.getPart() != null ? member.getPart().name() : null,
                member.getStatus(),
                member.getRole(),
                member.getAuthority(),
                member.getCreatedAt()
        );
    }
}
