package com.kusitms.website.domain.admin.dto.request;

import com.kusitms.website.domain.user.entity.MemberAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "어드민 권한 변경 요청")
public class AuthorityUpdateRequest {
    @Schema(description = "변경할 권한 (MEMBER/MANAGER/OWNER)", example = "MANAGER")
    private MemberAuthority authority;

    public AuthorityUpdateRequest(MemberAuthority authority) {
        this.authority = authority;
    }
}
