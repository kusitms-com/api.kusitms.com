package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.dto.request.AuthorityUpdateRequest;
import com.kusitms.website.domain.admin.dto.request.CurrentCardinalUpdateRequest;
import com.kusitms.website.domain.admin.dto.response.CurrentCardinalUpdateResponse;
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
@Tag(name = "Admin Owner", description = "OWNER 전용 API : 운영진(MANAGER)/OWNER 권한 부여 및 회수, 현재 기수 변경")
public class OwnerController {
    private final MemberAuthorityService memberAuthorityService;
    private final CardinalAdminService cardinalAdminService;

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

    // 전체 회원의 YB/OB가 한 번에 바뀌는 작업이라 OWNER만 허용한다
    @PutMapping("/current-cardinal")
    @Operation(summary = "현재 기수 변경", description = "현재 활동 기수를 변경합니다. 변경 즉시 전체 회원의 YB/OB를 기수 기준으로 다시 계산합니다. (가입 기수 >= 현재 기수: YB, 그 외: OB)")
    public ResponseEntity<BaseResponse<CurrentCardinalUpdateResponse>> updateCurrentCardinal(
            @RequestBody CurrentCardinalUpdateRequest request
    ) {
        return ResponseEntity.ok(new BaseResponse<>(cardinalAdminService.updateCurrentCardinal(request.getCardinal())));
    }
}
