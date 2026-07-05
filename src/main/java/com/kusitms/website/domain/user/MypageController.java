package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.request.AccountProfileUpdateRequest;
import com.kusitms.website.domain.user.dto.request.MentoringReviewCreateRequest;
import com.kusitms.website.domain.user.dto.request.OBProfileUpdateRequest;
import com.kusitms.website.domain.user.dto.request.OBProfileVisibilityUpdateRequest;
import com.kusitms.website.domain.user.dto.request.OBScheduleUpdateRequest;
import com.kusitms.website.domain.user.dto.request.PasswordChangeRequest;
import com.kusitms.website.domain.user.dto.response.AccountProfileResponse;
import com.kusitms.website.domain.user.dto.response.ApplicationRejectionReasonResponse;
import com.kusitms.website.domain.user.dto.response.OBProfileResponse;
import com.kusitms.website.domain.user.dto.response.OBScheduleResponse;
import com.kusitms.website.domain.user.dto.response.YBMypageResponse;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "Mypage", description = "마이페이지 API")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping
    @Operation(summary = "YB 마이페이지 조회", description = "로그인한 YB 사용자의 마이페이지 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<YBMypageResponse>> getYBMypage() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(mypageService.getYBMypage(userId)));
    }

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

    @PutMapping(value = "/account", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 계정 정보 수정", description = "로그인한 사용자의 계정 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<AccountProfileResponse>> updateAccountProfile(
            @RequestPart("accountProfileUpdateRequest") @Valid AccountProfileUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(
                mypageService.updateAccountProfile(userId, request, profileImage)));
    }

    @PutMapping("/account/password")
    @Operation(summary = "비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse> changePassword(
            @RequestBody @Valid PasswordChangeRequest request) {
        Long userId = getAuthenticatedUserId();
        mypageService.changePassword(userId, request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @GetMapping("/applications/{applicationId}/rejection-reason")
    @Operation(summary = "거절 사유 조회", description = "로그인한 사용자의 거절된 멘토링 신청 사유를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<ApplicationRejectionReasonResponse>> getApplicationRejectionReason(
            @PathVariable Long applicationId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(
                mypageService.getApplicationRejectionReason(userId, applicationId)));
    }

    @PostMapping("/reviews")
    @Operation(summary = "멘토링 후기 작성", description = "완료된 멘토링에 대한 후기를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "작성 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse> createMentoringReview(
            @RequestBody @Valid MentoringReviewCreateRequest request) {
        Long userId = getAuthenticatedUserId();
        mypageService.createMentoringReview(userId, request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @GetMapping("/ob/profile")
    @Operation(summary = "OB 프로필 조회", description = "로그인한 OB 사용자의 멘토 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<OBProfileResponse>> getOBProfile() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(mypageService.getOBProfile(userId)));
    }

    @PutMapping(value = "/ob/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "OB 프로필 저장", description = "로그인한 OB 사용자의 멘토 프로필 정보를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "저장 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<OBProfileResponse>> updateOBProfile(
            @RequestPart("obProfileUpdateRequest") @Valid OBProfileUpdateRequest request,
            @RequestPart(value = "mentorProfileImage", required = false) MultipartFile mentorProfileImage) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(
                mypageService.updateOBProfile(userId, request, mentorProfileImage)));
    }

    @PutMapping("/ob/profile/visibility")
    @Operation(summary = "OB 프로필 공개 토글", description = "멘토링 신청 받기 토글 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "변경 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<OBProfileResponse>> updateOBProfileVisibility(
            @RequestBody @Valid OBProfileVisibilityUpdateRequest request) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(
                mypageService.updateOBProfileVisibility(userId, request)));
    }

    @GetMapping("/ob/schedule")
    @Operation(summary = "OB 가능 시간 조회", description = "로그인한 OB 사용자의 가능 시간 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<OBScheduleResponse>> getOBSchedule() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(mypageService.getOBSchedule(userId)));
    }

    @PutMapping("/ob/schedule")
    @Operation(summary = "OB 가능 시간 저장", description = "로그인한 OB 사용자의 날짜별 가능 시간을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "저장 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<OBScheduleResponse>> updateOBSchedule(
            @RequestBody @Valid OBScheduleUpdateRequest request) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(
                mypageService.updateOBSchedule(userId, request)));
    }

    @DeleteMapping("/account")
    @Operation(summary = "회원탈퇴", description = "로그인한 사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "탈퇴 불가"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse> withdrawAccount() {
        Long userId = getAuthenticatedUserId();
        mypageService.withdrawAccount(userId);
        return ResponseEntity.ok(new BaseResponse());
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
