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
    @Schema(description = "배너 상태", example = "모집 마감/운영진 모집/학회원 모집")
    private String bannerStatus;

    @Schema(description = "배너 내용", example = "KUSITMS 27기 리크루팅 종료")
    private String bannerContent;

    @Schema(description = "누적 학회원 수", example = "1432")
    private Long memberCount;

    @Schema(description = "누적 프로젝트 수", example = "322")
    private Long projectCount;

    @Schema(description = "누적 대학 수", example = "78")
    private Long universityCount;

    @Schema(description = "학회 소개 영상", example = "https://www.youtube.com/")
    private String introYoutubeLink;

    @Schema(description = "파트너사 소개 이미지 URL")
    private String partnerLogoUrl;

    @Schema(description = "학회 운영진 소개")
    private List<ManagementTeamResponse> teams;

    @Schema(description = "전문가 초청 강연자 소개")
    private List<ExpertLectureResponse> expertLecture;

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
