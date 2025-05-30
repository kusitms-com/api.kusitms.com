package com.kusitms.website.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kusitms.website.domain.admin.entity.TMPMeetupProject;
import com.kusitms.website.domain.project.entity.MeetupProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import static com.kusitms.website.global.util.S3Util.s3Url;

@Getter
@Schema
public class MeetupDetailResponse {
    @JsonProperty("meetup_id")
    @Schema(description = "밋업 ID")
    private Long meetupId;

    @Schema(description = "프로젝트 진행 기수")
    private int cardinal;

    @Schema(description = "프로젝트 이름")
    private String name;

    @Schema(description = "프로젝트 소개")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String intro;

    @Schema(description = "프로젝트 구현 유형")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    @JsonProperty("one_line_intro")
    @Schema(description = "프로젝트 한 줄 소개")
    private String oneLineIntro;

    @JsonProperty("logo_url")
    @Schema(description = "로고 이미지 URL")
    private String logoUrl;

    @JsonProperty("poster_url")
    @Schema(description = "아이템 포스터 이미지 URL")
    private String posterUrl;

    @JsonProperty("web_thumbnail_url")
    @Schema(description = "웹 썸네일 이미지 URL")
    private String webThumbnailUrl;

    @JsonProperty("behance_url")
    @Schema(description = "비핸스 URL")
    private String behanceUrl;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate startDate;

    @JsonProperty("end_date")
    @Schema(description = "프로젝트 종료 날짜")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate endDate;

    @Schema(description = "프로젝트 참여 팀원")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MeetupTeamResponse team;

    @Setter
    @Schema(description = "프로젝트 관련 태그")
    @JsonProperty("tags")
    private List<String> tags;

    public MeetupDetailResponse(MeetupProject meetup, boolean isDetail) {
        this.meetupId = meetup.getMeetupId();
        this.cardinal = meetup.getMeetupId() == 55 ? 30 : meetup.getCardinal();
        this.name = meetup.getName();
        this.posterUrl = s3Url + meetup.getPosterUrl();
        this.webThumbnailUrl = s3Url + meetup.getWebThumbnailUrl();
        this.logoUrl = s3Url + meetup.getLogoUrl();
        this.oneLineIntro = meetup.getOneLineIntro();
        this.behanceUrl = meetup.getBehanceUrl();
        this.instagramUrl = meetup.getInstagramUrl();
        this.githubUrl = meetup.getGithubUrl();
        this.appUrl = meetup.getAppUrl();

        if(isDetail) {
            this.type = meetup.getType().getName();
            this.intro = meetup.getIntro();
            this.startDate = meetup.getStartDate();
            this.endDate = meetup.getEndDate();
            this.team = new MeetupTeamResponse(meetup.getTeam(), meetup.getTeamName());
        }
    }

    public MeetupDetailResponse(TMPMeetupProject meetup, boolean isDetail) {
        this.meetupId = meetup.getMeetupId();
        this.cardinal = meetup.getCardinal();
        this.name = meetup.getName();
        this.posterUrl = s3Url + meetup.getPosterUrl();
        this.logoUrl = s3Url + meetup.getLogoUrl();
        this.oneLineIntro = meetup.getOneLineIntro();
        this.instagramUrl = meetup.getInstagramUrl();
        this.githubUrl = meetup.getGithubUrl();
        this.appUrl = meetup.getAppUrl();

        if(isDetail) {
            this.type = meetup.getType().getName();
            this.intro = meetup.getIntro();
            this.startDate = meetup.getStartDate();
            this.endDate = meetup.getEndDate();
            this.team = new MeetupTeamResponse(meetup.getTeamName(), meetup.getTeam());
        }
    }
}
