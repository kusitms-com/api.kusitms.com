package com.kusitms.website.domain.project.dto.request;

import com.amazonaws.util.StringUtils;
import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.project.entity.MeetupTeam;
import com.kusitms.website.domain.project.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class MeetupTeamRequest {
    @Schema(description = "팀")
    private String team;
    @Schema(description = "이름")
    private String name;

    public MeetupTeam toEntity(MeetupProject meetup) {
        return MeetupTeam.builder()
                .meetup(meetup)
                .team(Team.valueOf(StringUtils.upperCase(team)))
                .name(name)
                .build();
    }
}
