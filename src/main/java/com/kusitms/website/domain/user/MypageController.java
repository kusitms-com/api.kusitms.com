package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.response.AccountProfileResponse;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "Mypage", description = "마이페이지 API")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/account")
    @Operation(summary = "내 계정 정보 조회", description = "로그인한 사용자의 계정 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<AccountProfileResponse>> getAccountProfile() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(mypageService.getAccountProfile(userId)));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getPk();
    }
}
