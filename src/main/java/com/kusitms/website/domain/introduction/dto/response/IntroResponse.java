package com.kusitms.website.domain.introduction.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.Introduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema
public class IntroResponse {
    @JsonProperty("banner_status")
    @Schema(description = "배너 상태", example = "모집 마감/운영진 모집/학회원 모집")
    private String bannerStatus;

    @JsonProperty("banner_content")
    @Schema(description = "배너 내용", example = "KUSITMS 27기 리크루팅 종료")
    private String bannerContent;

    @Schema(description = "슬로건")
    private String slogan;

    @JsonProperty("banner_image_url")
    @Schema(description = "배너 이미지 URL")
    private String bannerImageUrl;

    @JsonProperty("member_count")
    @Schema(description = "누적 학회원 수", example = "1432")
    private Long memberCount;

    @JsonProperty("project_count")
    @Schema(description = "누적 프로젝트 수", example = "322")
    private Long projectCount;

    @JsonProperty("university_count")
    @Schema(description = "누적 대학 수", example = "78")
    private Long universityCount;

    @JsonProperty("intro_youtube_link")
    @Schema(description = "학회 소개 영상", example = "https://www.youtube.com/")
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

    @JsonProperty("partner_logo_urls")
    @Schema(description = "파트너사 로고 이미지 URL 목록")
    private List<String> partnerLogoUrls;

    @JsonProperty("meetup_image_urls")
    @Schema(description = "밋업 이미지 URL 목록")
    private List<String> meetupImageUrls;

    @Schema(description = "학회 운영진 소개")
    private List<ManagementTeamResponse> teams;

    @JsonProperty("expert_lecture")
    @Schema(description = "전문가 초청 강연자 소개")
    private List<ExpertLectureResponse> expertLecture;

    @JsonProperty("ob_lecture")
    @Schema(description = "ob 초청 강연자 소개")
    private List<OBLectureResponse> obLecture;

    @Schema(description = "활동 소개 목록")
    private List<ActivityResponse> activities;

    @JsonProperty("sponsor_image_urls")
    @Schema(description = "후원사 이미지 URL 목록")
    private List<String> sponsorImageUrls;

    public static IntroResponse fromEntity(Introduction introduction,
                                           List<String> partnerLogoUrls,
                                           List<String> meetupImageUrls,
                                           List<ManagementTeamResponse> managementTeam,
                                           List<ExpertLectureResponse> expertLecture,
                                           List<OBLectureResponse> obLecture,
                                           List<ActivityResponse> activities,
                                           List<String> sponsorImageUrls) {
        return IntroResponse.builder()
                .bannerStatus(introduction.getBannerStatus() != null ? introduction.getBannerStatus().getName() : null)
                .bannerContent(introduction.getBannerStatus() != null
                        ? "KUSITMS" + introduction.getBannerCardinal() + "기 " + introduction.getBannerStatus().getContent()
                        : null)
                .slogan(introduction.getSlogan())
                .bannerImageUrl(introduction.getBannerImageUrl())
                .memberCount(introduction.getMemberCount())
                .projectCount(introduction.getProjectCount())
                .universityCount(introduction.getUniversityCount())
                .introYoutubeLink(introduction.getIntroYoutubeLink())
                .planningImageUrl(introduction.getPlanningImageUrl())
                .designImageUrl(introduction.getDesignImageUrl())
                .frontendImageUrl(introduction.getFrontendImageUrl())
                .backendImageUrl(introduction.getBackendImageUrl())
                .partnerLogoUrls(partnerLogoUrls)
                .meetupImageUrls(meetupImageUrls)
                .teams(managementTeam)
                .expertLecture(expertLecture)
                .obLecture(obLecture)
                .activities(activities)
                .sponsorImageUrls(sponsorImageUrls)
                .build();
    }
}