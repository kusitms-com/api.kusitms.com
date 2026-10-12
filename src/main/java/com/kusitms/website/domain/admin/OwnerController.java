package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.dto.request.AuthorityUpdateRequest;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/owner")
@PreAuthorize("hasRole('OWNER')")
@Tag(name = "Admin Owner", description = "OWNER 전용 API : 운영진(MANAGER)/OWNER 권한 부여 및 회수")
public class OwnerController {
    private final MemberAuthorityService memberAuthorityService;

    @PatchMapping("/members/{userId}/authority")
    @Operation(summary = "회원 권한 변경", description = "회원의 어드민 권한을 MEMBER/MANAGER/OWNER로 변경합니다. 자기 자신의 권한은 변경할 수 없고, 마지막 OWNER는 강등할 수 없습니다.")
    public ResponseEntity<BaseResponse> updateAuthority(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long userId,
            @RequestBody AuthorityUpdateRequest request
    ) {
        memberAuthorityService.changeAuthority(principal.getPk(), userId, request.getAuthority());
        return ResponseEntity.ok(new BaseResponse());
    }
}
