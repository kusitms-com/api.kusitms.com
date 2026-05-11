package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.introduction.IntroService;
import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.blog.dto.request.BlogReviewRequest;
import com.kusitms.website.domain.project.dto.request.CorporateRequest;
import com.kusitms.website.domain.project.dto.request.MeetupRequest;
import com.kusitms.website.domain.review.dto.request.ReviewRequest;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
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

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "어드민 페이지 API : 테스트 단계이므로 /admin으로 요청하는 모든 데이터는 현재 배포된 사이트와 독립적임")
public class AdminController {
    private final IntroService introService;
    private final AdminService adminService;
    private final JwtTokenProvider jwtTokenProvider;

    @Hidden
    @PostMapping(value = "/admin/introductions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> addIntroduction(@ModelAttribute IntroRequest request) {
        introService.save(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PutMapping(value = "/admin/introductions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "학회 소개 정보 수정", description = "학회 소개 페이지의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> updateIntroduction(@ModelAttribute IntroRequest request, HttpServletRequest httpRequest) {
        String token = jwtTokenProvider.getJwtFromRequest(httpRequest);
        introService.updateIntroduction(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PostMapping(value = "/admin/projects/corporate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기업 프로젝트 정보 등록", description = "기업 프로젝트의 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> addCorporateProject(@ModelAttribute CorporateRequest request) {
        adminService.saveCorporate(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PutMapping(value = "/admin/projects/corporate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기업 프로젝트 정보 수정", description = "기업 프로젝트의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> updateCorporateProject(@ModelAttribute CorporateRequest request) {
        adminService.updateCorporate(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @DeleteMapping(value = "/admin/projects/corporate/{id}")
    @Operation(summary = "기업 프로젝트 정보 삭제", description = "기업 프로젝트의 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> deleteCorporateProject(@PathVariable("id") Long corporateId) {
        adminService.deleteCorporate(corporateId);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PostMapping(value = "/admin/projects/meetup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "밋업데이 프로젝트 정보 등록", description = "밋업데이 프로젝트의 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> addMeetupProject(@ModelAttribute MeetupRequest request) {
        adminService.saveMeetup(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PutMapping(value = "/admin/projects/meetup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "밋업데이 프로젝트 정보 수정", description = "밋업데이 프로젝트의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> updateMeetupProject(@ModelAttribute MeetupRequest request) {
        adminService.updateMeetup(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @DeleteMapping(value = "/admin/projects/meetup/{id}")
    @Operation(summary = "밋업데이 정보 삭제", description = "밋업데이 프로젝트의 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> deleteMeetupProject(@PathVariable("id") Long meetupId) {
        adminService.deleteMeetup(meetupId);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PostMapping(value = "/admin/review")
    @Operation(summary = "학회원 후기 등록", description = "학회원 후기를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> addReview(@RequestBody ReviewRequest request) {
        adminService.saveReview(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PutMapping(value = "/admin/review")
    @Operation(summary = "학회원 후기 수정", description = "학회원 후기를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> updateReview(@RequestBody ReviewRequest request) {
        adminService.updateReview(request);
        return ResponseEntity.ok(new BaseResponse());
    }

    @DeleteMapping(value = "/admin/review/{id}")
    @Operation(summary = "학회원 후기 삭제", description = "학회원 후기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> deleteReview(@PathVariable("id") Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.ok(new BaseResponse());
    }

    @GetMapping("/admin/review")
    @Operation(summary = "리뷰 리스트 (test db)", description = "리뷰의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> getReviews() {
        return ResponseEntity.ok(new BaseResponse(adminService.getReviews()));
    }

    @GetMapping("/admin/meetup")
    @Operation(summary = "밋업데이 프로젝트 리스트(test db)", description = "밋업데이 프로젝트의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),

    })
    public ResponseEntity<BaseResponse> getMeetupProjects() {
        return ResponseEntity.ok(new BaseResponse(adminService.getMeetupProjects()));
    }

    @GetMapping("/admin/meetup/{meetup_id}")
    @Operation(summary = "밋업데이 프로젝트 상세 조회(test db)", description = "밋업데이 프로젝트의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> getMeetupProject(@PathVariable(name = "meetup_id") Long meetupId) {
        return ResponseEntity.ok(new BaseResponse(adminService.getMeetupProject(meetupId)));
    }

    @GetMapping("/admin/corporate")
    @Operation(summary = "기업 프로젝트 리스트(test db)", description = "기업 프로젝트의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> getCorporateProjects() {
        return ResponseEntity.ok(new BaseResponse(adminService.getCorporateProjects()));
    }

    @GetMapping("/admin/corporate/{id}")
    @Operation(summary = "기업 프로젝트 상세 조회(test db)", description = "기업 프로젝트의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> getCorporateProject(@PathVariable("id") Long corporateId) {
        return ResponseEntity.ok(new BaseResponse(adminService.getCorporateProject(corporateId)));
    }

    @GetMapping("/admin/blog-review")
    @Operation(summary = "블로그 후기 리스트(test db)", description = "블로그 후기의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> getBlogReviews() {
        return ResponseEntity.ok(new BaseResponse(adminService.getBlogReviews()));
    }

    @PostMapping(value = "/admin/blog-review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "블로그 후기 등록(test db)", description = "블로그 후기를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "500", description = "INTER SERVER ERROR", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    public ResponseEntity<BaseResponse> addBlogReview(@ModelAttribute BlogReviewRequest request) {
        adminService.saveBlogReview(request);
        return ResponseEntity.ok(new BaseResponse());
    }
}
