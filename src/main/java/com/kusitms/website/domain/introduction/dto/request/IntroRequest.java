package com.kusitms.website.domain.introduction.dto.request;

import com.kusitms.website.domain.introduction.entity.BannerStatus;
import com.kusitms.website.domain.introduction.entity.Introduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Schema(description = "학회 소개 요청")
public class IntroRequest {
    @NotNull(message = "배너 기수는 필수입니다.")
    @Schema(description = "배너 내에 삽입되는 기수", example = "34", required = true)
    private Long bannerCardinal;

    @NotNull(message = "배너 상태는 필수입니다.")
    @Schema(description = "배너 상태 (대문자 영문 코드)", example = "CLOSE", required = true)
    private BannerStatus bannerStatus;

    @NotNull(message = "누적 학회원 수는 필수입니다.")
    @Schema(description = "누적 학회원 수", example = "1432", required = true)
    private Long memberCount;

    @NotNull(message = "누적 프로젝트 결과물 수는 필수입니다.")
    @Schema(description = "누적 프로젝트 결과물 수", example = "322", required = true)
    private Long projectCount;

    @NotNull(message = "참여 대학 수는 필수입니다.")
    @Schema(description = "참여 대학 수", example = "78", required = true)
    private Long universityCount;

    @NotBlank(message = "학회 소개 영상 링크는 필수입니다.")
    @Schema(description = "학회 소개 영상", example = "https://www.youtube.com/", required = true)
    private String introYoutubeLink;

    @Schema(description = "기획 이미지 파일 (수정 시 생략하면 기존 이미지 유지)")
    private MultipartFile planningImage;

    @Schema(description = "디자인 이미지 파일 (수정 시 생략하면 기존 이미지 유지)")
    private MultipartFile designImage;

    @Schema(description = "프론트 이미지 파일 (수정 시 생략하면 기존 이미지 유지)")
    private MultipartFile frontendImage;

    @Schema(description = "백엔드 이미지 파일 (수정 시 생략하면 기존 이미지 유지)")
    private MultipartFile backendImage;

    @Schema(description = "학회 운영진 소개 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 수정·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<ManagementTeamRequest> teams;

    @Schema(description = "전문가 초청 강연자 소개 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 수정·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<ExpertLectureRequest> expertLecture;

    @Schema(description = "OB 초청 강연자 소개 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 수정·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<OBLectureRequest> obLecture;

    @Schema(description = "파트너사(기업 프로젝트) 로고 목록 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 유지·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<IntroImageRequest> partnerLogos;

    @Schema(description = "밋업 이미지 목록 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 유지·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<IntroImageRequest> meetupImages;

    @Schema(description = "활동 소개 목록 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 수정·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<ActivityRequest> activities;

    @Schema(description = "후원사 로고 목록 (수정 시 생략하면 기존 목록 유지, 전달하면 id 기준으로 유지·추가하고 빠진 항목은 삭제, 요청 순서대로 정렬)")
    private List<IntroImageRequest> sponsors;

    public static Introduction from(IntroRequest request, String partnerImageUrl,
                                     String planningImageUrl, String designImageUrl,
                                     String frontendImageUrl, String backendImageUrl) {
        return Introduction.builder()
                .bannerCardinal(request.getBannerCardinal())
                .bannerStatus(request.getBannerStatus())
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
