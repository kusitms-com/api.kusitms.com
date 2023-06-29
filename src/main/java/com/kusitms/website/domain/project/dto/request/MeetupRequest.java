package com.kusitms.website.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kusitms.website.domain.admin.entity.TMPMeetupProject;
import com.kusitms.website.domain.project.entity.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Schema
public class MeetupRequest {
    @Schema(description = "프로젝트 ID (PUT API에서 사용)", example = "1")
    private Long meetupId;

    @Schema(description = "프로젝트 진행 기수", example = "26")
    private int cardinal;

    @Schema(description = "프로젝트 이름", example = "피크랩")
    private String name;

    @Schema(description = "프로젝트 소개", example = "피크랩은 이러한 서비스입니다.")
    private String intro;

    @Schema(description = "프로젝트 구현 유형", example = "APP / WEB")
    private ProjectType type;

    @Schema(description = "프로젝트 한 줄 소개", example = "피크랩은 이러한 서비스입니다.")
    private String oneLineIntro;

    @Schema(description = "인스타그램 URL", example = "https://www.instagram.com/kusitms_official/")
    private String instagramUrl;

    @Schema(description = "깃허브 URL", example = "https://github.com/kusitms-com")
    private String githubUrl;

    @Schema(description = "프로젝트 결과물 URL", example = "https://github.com/kusitms-com")
    private String appUrl;

    @Schema(description = "프로젝트 시작 날짜", example = "2022-02-12")
    private String startDate;

    @Schema(description = "프로젝트 종료 날짜", example = "2022-05-28")
    private String endDate;

    @Schema(description = "팀명", example = "오텐션")
    private String teamName;

    @Schema(description = "기획자 팀원")
    private List<String> planner = new ArrayList<>();

    @Schema(description = "디자이너 팀원")
    private List<String> designer = new ArrayList<>();

    @Schema(description = "프론트엔드 팀원")
    private List<String> frontend = new ArrayList<>();

    @Schema(description = "백엔드 팀원")
    private List<String> backend = new ArrayList<>();

    @Schema(description = "IOS 팀원")
    private List<String> ios = new ArrayList<>();

    @Schema(description = "ANDROID 팀원")
    private List<String> aos = new ArrayList<>();

    @Schema(description = "로고 이미지 파일")
    private MultipartFile logoFile;

    @Schema(description = "아이템 포스터 이미지 파일")
    private MultipartFile posterFile;

    public static TMPMeetupProject from(MeetupRequest request, String logoUrl, String posterUrl) {
        return TMPMeetupProject.builder()
                .cardinal(request.getCardinal())
                .name(request.getName())
                .intro(request.getIntro())
                .type(request.getType())
                .oneLineIntro(request.getOneLineIntro())
                .instagramUrl(request.getInstagramUrl())
                .githubUrl(request.getGithubUrl())
                .appUrl(request.getAppUrl())
                .startDate(LocalDate.parse(request.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(request.getEndDate(), DateTimeFormatter.ISO_DATE))
                .logoUrl(logoUrl)
                .posterUrl(posterUrl)
                .teamName(request.getTeamName())
                .build();
    }
}
