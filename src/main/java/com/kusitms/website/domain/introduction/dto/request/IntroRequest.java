package com.kusitms.website.domain.introduction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.introduction.entity.BannerStatus;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.ManageTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Schema
public class IntroRequest {
    @JsonProperty("banner_cardinal")
    @Schema(description = "배너 내에 삽입되는 기수", example = "27")
    private Long bannerCardinal;

    @JsonProperty("banner_status")
    @Schema(description = "배너 상태 (대문자 영문 코드)", example = "CLOSE/MANAGEMENT_RECRUIT/MEMBER_RECRUIT")
    private BannerStatus bannerStatus;

    @JsonProperty("member_count")
    @Schema(description = "누적 학회원 수 ", example = "1432")
    private Long memberCount;

    @JsonProperty("project_count")
    @Schema(description = "누적 프로젝트 결과물 수", example = "322")
    private Long projectCount;

    @JsonProperty("university_count")
    @Schema(description = "참여 대학 수", example = "78")
    private Long universityCount;

    @JsonProperty("intro_youtube_link")
    @Schema(description = "학회 소개 영상", example = "https://www.youtube.com/")
    private String introYoutubeLink;

    @JsonProperty("management_team")
    @Schema(description = "학회 운영진 소개")
    private List<ManagementTeamRequest> managementTeam;

    @JsonProperty("expert_lecture")
    @Schema(description = "전문가 초청 강연자 소개")
    private List<ExpertLectureRequest> expertLecture;

    @JsonProperty("ob_lecture")
    @Schema(description = "ob 초청 강연자 소개")
    private List<OBLectureRequest> obLecture;

    @JsonProperty("partner_logo_file")
    @Schema(description = "파트너사 소개 이미지 파일")
    private MultipartFile partnerLogoFile;

    public static Introduction from(IntroRequest request, String partnerImageUrl) {
        return Introduction.builder()
                .bannerCardinal(request.getBannerCardinal())
                .bannerStatus(request.getBannerStatus())
                .memberCount(request.getMemberCount())
                .projectCount(request.getProjectCount())
                .universityCount(request.getUniversityCount())
                .introYoutubeLink(request.getIntroYoutubeLink())
                .partnerImageUrl(partnerImageUrl)
                .build();
    }
}
