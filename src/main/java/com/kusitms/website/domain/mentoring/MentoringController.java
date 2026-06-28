package com.kusitms.website.domain.mentoring;

import com.kusitms.website.domain.mentoring.dto.request.MentoringApplyRequest;
import com.kusitms.website.domain.mentoring.dto.response.MentorDetailResponse;
import com.kusitms.website.domain.mentoring.dto.response.MentorListResponse;
import com.kusitms.website.domain.mentoring.dto.response.MentorMainResponse;
import com.kusitms.website.domain.mentoring.dto.response.MentoringReviewListResponse;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import com.kusitms.website.domain.mentoring.service.MentoringService;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.common.BaseResponse;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentoring")
@RequiredArgsConstructor
@Tag(name = "Mentoring", description = "멘토링 API")
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping
    @Operation(summary = "멘토링 메인", description = "활동 중인 멘토 카드 최대 4개를 랜덤으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public ResponseEntity<BaseResponse<MentorMainResponse>> getMainMentors() {
        return ResponseEntity.ok(new BaseResponse<>(mentoringService.getMainMentors()));
    }

    @GetMapping("/list")
    @Operation(summary = "멘토 리스트", description = "멘토 목록을 카테고리별로 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public ResponseEntity<BaseResponse<MentorListResponse>> getMentorList(
            @RequestParam(required = false) MentoringCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(new BaseResponse<>(mentoringService.getMentorList(category, page, size)));
    }

    @GetMapping("/{mentorId}")
    @Operation(summary = "멘토 세부정보", description = "멘토의 상세 정보를 조회합니다. 로그인 필수.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse<MentorDetailResponse>> getMentorDetail(
            @PathVariable Long mentorId) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(new BaseResponse<>(mentoringService.getMentorDetail(mentorId, userId)));
    }

    @GetMapping("/{mentorId}/reviews")
    @Operation(summary = "멘토링 후기 페이지네이션", description = "멘토의 후기를 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public ResponseEntity<BaseResponse<MentoringReviewListResponse>> getMentorReviews(
            @PathVariable Long mentorId,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(new BaseResponse<>(mentoringService.getMentorReviews(mentorId, page)));
    }

    @PostMapping("/{mentorId}/apply")
    @Operation(summary = "멘토링 신청", description = "멘토링 슬롯을 선택하여 신청합니다. 로그인 필수.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신청 성공"),
            @ApiResponse(responseCode = "400", description = "신청 실패"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<BaseResponse> applyMentoring(
            @PathVariable Long mentorId,
            @Valid @RequestBody MentoringApplyRequest request) {
        Long userId = getAuthenticatedUserId();
        mentoringService.applyMentoring(mentorId, userId, request);
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
