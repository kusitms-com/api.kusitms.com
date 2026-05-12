package com.kusitms.website.domain.introduction.dto.request;

import com.kusitms.website.domain.introduction.entity.BannerStatus;
import com.kusitms.website.domain.introduction.entity.Introduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Schema(description = "학회 소개 요청")
public class IntroRequest {
    @Schema(description = "배너 내에 삽입되는 기수", example = "27")
    private Long bannerCardinal;

    @Schema(description = "배너 상태 (대문자 영문 코드)", example = "CLOSE")
    private BannerStatus bannerStatus;

    @Schema(description = "슬로건", example = "큐시즘, 함께 성장하는 IT 학회")
    private String slogan;

    @Schema(description = "배너 이미지 파일")
    private MultipartFile bannerImageFile;

    @Schema(description = "누적 학회원 수", example = "1432")
    private Long memberCount;

    @Schema(description = "누적 프로젝트 결과물 수", example = "322")
    private Long projectCount;

    @Schema(description = "참여 대학 수", example = "78")
    private Long universityCount;

    @Schema(description = "학회 소개 영상", example = "https://www.youtube.com/")
    private String introYoutubeLink;

    @Schema(description = "기획 이미지 파일")
    private MultipartFile planningImage;

    @Schema(description = "디자인 이미지 파일")
    private MultipartFile designImage;

    @Schema(description = "프론트 이미지 파일")
    private MultipartFile frontendImage;

    @Schema(description = "백엔드 이미지 파일")
    private MultipartFile backendImage;

    @Schema(description = "학회 운영진 소개")
    private List<ManagementTeamRequest> teams;

    @Schema(description = "전문가 초청 강연자 소개")
    private List<ExpertLectureRequest> expertLecture;

    @Schema(description = "OB 초청 강연자 소개")
    private List<OBLectureRequest> obLecture;

    @Schema(description = "파트너사 로고 이미지 파일 목록")
    private List<MultipartFile> partnerLogoFiles;

    @Schema(description = "밋업 이미지 파일 목록")
    private List<MultipartFile> meetupImages;

    @Schema(description = "활동 소개 목록")
    private List<ActivityRequest> activities;

    @Schema(description = "후원사 이미지 파일 목록")
    private List<MultipartFile> sponsors;

    public static Introduction from(IntroRequest request, String partnerImageUrl,
                                     String bannerImageUrl,
                                     String planningImageUrl, String designImageUrl,
                                     String frontendImageUrl, String backendImageUrl) {
        return Introduction.builder()
                .bannerCardinal(request.getBannerCardinal())
                .bannerStatus(request.getBannerStatus())
                .slogan(request.getSlogan())
                .bannerImageUrl(bannerImageUrl)
                .memberCount(request.getMemberCount())
                .projectCount(request.getProjectCount())
                .universityCount(request.getUniversityCount())
                .introYoutubeLink(request.getIntroYoutubeLink())
                .partnerImageUrl(partnerImageUrl)
                .planningImageUrl(planningImageUrl)
                .designImageUrl(designImageUrl)
                .frontendImageUrl(frontendImageUrl)
                .backendImageUrl(backendImageUrl)
                .build();
    }
}
