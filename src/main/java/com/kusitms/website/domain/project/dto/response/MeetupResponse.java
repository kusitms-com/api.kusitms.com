package com.kusitms.website.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MeetupResponse {
    @JsonProperty("meetup_count")
    @Schema(description = "밋업 프로젝트 개수")
    private int meetupCount;

    @JsonProperty("meetup_list")
    @Schema(description = "밋업 프로젝트 리스트")
    private List<MeetupDetailResponse> meetupList;
}
