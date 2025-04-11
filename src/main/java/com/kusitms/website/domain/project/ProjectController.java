package com.kusitms.website.domain.project;

import com.kusitms.website.domain.project.service.CorporateService;
import com.kusitms.website.domain.project.service.MeetupService;
import com.kusitms.website.global.common.BaseResponse;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 API Document")
public class ProjectController {
    private final MeetupService meetupService;
    private final CorporateService corporateService;

    @GetMapping("/meetup")
    @Operation(summary = "밋업데이 프로젝트 리스트", description = "밋업데이 프로젝트의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MeetupDetailResponse.class)))
    })
    public ResponseEntity<BaseResponse> getMeetupProjects(
            @RequestParam(required = false) Integer cardinal,
            @RequestParam(defaultValue = "desc") String order) {
        return ResponseEntity.ok(new BaseResponse(meetupService.getMeetupProjects(order, cardinal)));
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
        return ResponseEntity.ok(new BaseResponse(meetupService.getMeetupProject(meetupId)));
    }

    @GetMapping("/corporate")
    @Operation(summary = "기업 프로젝트 리스트", description = "기업 프로젝트의 모든 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CorporateResponse.class))),
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CorporateDetailResponse.class)))
    })
    public ResponseEntity<BaseResponse> getCorporateProjects(
            @RequestParam(required = false) Integer cardinal,
            @RequestParam(defaultValue = "desc") String order) {
        return ResponseEntity.ok(new BaseResponse(corporateService.getCorporateProjects(order, cardinal)));
    }
}
