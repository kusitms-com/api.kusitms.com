package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.global.util.S3Util;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema
public class AdminIntroResponse {
    @JsonProperty("banner_cardinal")
    @Schema(description = "배너 기수")
    private Long bannerCardinal;

    @JsonProperty("banner_status")
    @Schema(description = "배너 상태")
    private String bannerStatus;

    @JsonProperty("member_count")
    @Schema(description = "누적 학회원 수")
    private Long memberCount;

    @JsonProperty("project_count")
    @Schema(description = "누적 프로젝트 수")
    private Long projectCount;

    @JsonProperty("university_count")
    @Schema(description = "참여 대학 수")
    private Long universityCount;

    @JsonProperty("intro_youtube_link")
    @Schema(description = "학회 소개 영상")
    private String introYoutubeLink;

    @JsonProperty("planning_image_url")
    @Schema(description = "기획 이미지 URL")
    private String planningImageUrl;

    @JsonProperty("design_image_url")
    @Schema(description = "디자인 이미지 URL")
    private String designImageUrl;

    @JsonProperty("frontend_image_url")
    @Schema(description = "프론트 이미지 URL")
    private String frontendImageUrl;

    @JsonProperty("backend_image_url")
    @Schema(description = "백엔드 이미지 URL")
    private String backendImageUrl;

    @JsonProperty("partner_logos")
    @Schema(description = "파트너사(기업 프로젝트) 로고 목록")
    private List<IntroImageResponse> partnerLogos;

    @JsonProperty("meetup_images")
    @Schema(description = "밋업 이미지 목록")
    private List<IntroImageResponse> meetupImages;

    @Schema(description = "활동 소개 목록")
    private List<ActivityResponse> activities;

    @Schema(description = "운영진 소개")
    private List<ManagementTeamResponse> teams;

    @JsonProperty("expert_lecture")
    @Schema(description = "전문가 초청 강연자 소개")
    private List<ExpertLectureResponse> expertLecture;

    @JsonProperty("ob_lecture")
    @Schema(description = "OB 초청 강연자 소개")
    private List<OBLectureResponse> obLecture;

    @Schema(description = "후원사 로고 목록")
    private List<IntroImageResponse> sponsors;

    public static AdminIntroResponse fromEntity(Introduction intro,
                                                 List<IntroImageResponse> partnerLogos,
                                                 List<IntroImageResponse> meetupImages,
                                                 List<ActivityResponse> activities,
                                                 List<ManagementTeamResponse> teams,
                                                 List<ExpertLectureResponse> expertLecture,
                                                 List<OBLectureResponse> obLecture,
                                                 List<IntroImageResponse> sponsors) {
        return AdminIntroResponse.builder()
                .bannerCardinal(intro.getBannerCardinal())
                .bannerStatus(intro.getBannerStatus() != null ? intro.getBannerStatus().name() : null)
                .memberCount(intro.getMemberCount())
                .projectCount(intro.getProjectCount())
                .universityCount(intro.getUniversityCount())
                .introYoutubeLink(intro.getIntroYoutubeLink())
                .planningImageUrl(S3Util.toUrl(intro.getPlanningImageUrl()))
                .designImageUrl(S3Util.toUrl(intro.getDesignImageUrl()))
                .frontendImageUrl(S3Util.toUrl(intro.getFrontendImageUrl()))
                .backendImageUrl(S3Util.toUrl(intro.getBackendImageUrl()))
                .partnerLogos(partnerLogos)
                .meetupImages(meetupImages)
                .activities(activities)
                .teams(teams)
                .expertLecture(expertLecture)
                .obLecture(obLecture)
                .sponsors(sponsors)
                .build();
    }
}