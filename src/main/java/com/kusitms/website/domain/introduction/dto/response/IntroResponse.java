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

    @JsonProperty("parent_logo_url")
    @Schema(description = "파트너사 소개 이미지 URL")
    private String partnerLogoUrl;

    @Schema(description = "학회 운영진 소개")
    private List<ManagementTeamResponse> teams;

    @JsonProperty("expert_lecture")
    @Schema(description = "전문가 초청 강연자 소개")
    private List<ExpertLectureResponse> expertLecture;

    @JsonProperty("ob_lecture")
    @Schema(description = "ob 초청 강연자 소개")
    private List<OBLectureResponse> obLecture;

    public static IntroResponse fromEntity(Introduction introduction, List<ManagementTeamResponse> managementTeam,
                               List<ExpertLectureResponse> expertLecture, List<OBLectureResponse> obLecture) {
        return IntroResponse.builder()
                .bannerStatus(introduction.getBannerStatus().getName())
                .bannerContent("KUSITMS"
                        + introduction.getBannerCardinal() + "기 "
                        + introduction.getBannerStatus().getContent())
                .memberCount(introduction.getMemberCount())
                .projectCount(introduction.getProjectCount())
                .universityCount(introduction.getUniversityCount())
                .introYoutubeLink(introduction.getIntroYoutubeLink())
                .partnerLogoUrl(introduction.getPartnerImageUrl())
                .teams(managementTeam)
                .expertLecture(expertLecture)
                .obLecture(obLecture)
                .build();
    }
}
