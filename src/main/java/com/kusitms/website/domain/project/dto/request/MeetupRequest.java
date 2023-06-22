package com.kusitms.website.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.project.entity.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema
public class MeetupRequest {
    @Schema(description = "프로젝트 진행 기수")
    private int cardinal;

    @Schema(description = "프로젝트 이름")
    private String name;

    @Schema(description = "프로젝트 소개")
    private String intro;

    @Schema(description = "프로젝트 구현 유형")
    private String type;

    @JsonProperty("one_line_intro")
    @Schema(description = "프로젝트 한 줄 소개")
    private String oneLineIntro;

    @JsonProperty("instagram_url")
    @Schema(description = "인스타그램 URL")
    private String instagramUrl;

    @JsonProperty("github_url")
    @Schema(description = "깃허브 URL")
    private String githubUrl;

    @JsonProperty("app_url")
    @Schema(description = "프로젝트 결과물 URL")
    private String appUrl;

    @JsonProperty("start_date")
    @Schema(description = "프로젝트 시작 날짜")
    private LocalDate startDate;

    @JsonProperty("end_date")
    @Schema(description = "프로젝트 종료 날짜")
    private LocalDate endDate;

    @JsonProperty("team_name")
    @Schema(description = "팀명")
    private String teamName;

    @JsonProperty("member_list")
    private List<MeetupTeamRequest> meetupTeamRequests;

    public MeetupProject toEntity(String logoUrl, String posterUrl) {
        return MeetupProject.builder()
                .cardinal(cardinal)
                .name(name)
                .intro(intro)
                .type(ProjectType.from(type))
                .oneLineIntro(oneLineIntro)
                .instagramUrl(instagramUrl)
                .githubUrl(githubUrl)
                .appUrl(appUrl)
                .startDate(startDate)
                .endDate(endDate)
                .logoUrl(logoUrl)
                .posterUrl(posterUrl)
                .teamName(teamName)
                .build();
    }


}
