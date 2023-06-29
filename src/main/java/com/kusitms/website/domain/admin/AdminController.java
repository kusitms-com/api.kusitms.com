package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.introduction.IntroService;
import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.domain.review.ReviewService;
import com.kusitms.website.domain.review.dto.response.ReviewDetailResponse;
import com.kusitms.website.domain.review.dto.response.ReviewResponse;
import com.kusitms.website.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "어드민 페이지 API : 테스트 단계이므로 /admin으로 요청하는 모든 데이터는 현재 배포된 사이트와 독립적임")
public class AdminController {
    private final IntroService introService;
    private final AdminService adminService;

    @Hidden
    @PostMapping(value = "/introductions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> addIntroduction(@ModelAttribute IntroRequest request) {
        introService.save(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PutMapping(value = "/introductions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "학회 소개 정보 수정", description = "학회 소개 페이지의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공")
    })
    public ResponseEntity<BaseResponse> updateIntroduction(@ModelAttribute IntroRequest request) {
        introService.updateIntroduction(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @GetMapping
    @Operation(summary = "리뷰 리스트", description = "리뷰의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ReviewDetailResponse.class))),
    })
    public ResponseEntity<BaseResponse> getReviews() {
        return ResponseEntity.ok(new BaseResponse(adminService.getReviews()));
    }

    @GetMapping("/meetup")
    @Operation(summary = "밋업데이 프로젝트 리스트", description = "밋업데이 프로젝트의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupDetailResponse.class)))
    })
    public ResponseEntity<BaseResponse> getMeetupProjects() {
        return ResponseEntity.ok(new BaseResponse(adminService.getMeetupProjects()));
    }

    @GetMapping("/meetup/{meetup_id}")
    @Operation(summary = "밋업데이 프로젝트 상세 조회", description = "밋업데이 프로젝트의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupDetailResponse.class)))
    })
    public ResponseEntity<BaseResponse> getMeetupProject(@PathVariable(name = "meetup_id") Long meetupId) {
        return ResponseEntity.ok(new BaseResponse(adminService.getMeetupProject(meetupId)));
    }

    @GetMapping("/corporate")
    @Operation(summary = "기업 프로젝트 리스트", description = "기업 프로젝트의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CorporateResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CorporateDetailResponse.class)))
    })
    public ResponseEntity<BaseResponse> getCorporateProjects() {
        return ResponseEntity.ok(new BaseResponse(adminService.getCorporateProjects()));
    }
}
