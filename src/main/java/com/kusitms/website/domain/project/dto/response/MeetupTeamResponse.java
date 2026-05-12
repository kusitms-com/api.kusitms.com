package com.kusitms.website.domain.project.dto.response;

import com.kusitms.website.domain.admin.entity.TMPMeetupTeam;
import com.kusitms.website.domain.project.entity.MeetupTeam;
import com.kusitms.website.domain.project.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Schema
public class MeetupTeamResponse {
    @Schema(description = "팀 이름")
    private String name;
    @Schema(description = "기획자 팀원")
    private List<String> planner;
    @Schema(description = "디자이너 팀원")
    private List<String> designer;
    @Schema(description = "프론트엔드 팀원")
    private List<String> frontend;
    @Schema(description = "백엔드 팀원")
    private List<String> backend;
    @Schema(description = "IOS 팀원")
    private List<String> ios;
    @Schema(description = "ANDROID 팀원")
    private List<String> aos;

    public MeetupTeamResponse(List<MeetupTeam> team, String name) {
        this.name = name;
        for (MeetupTeam t : team) {
            addMember(t.getTeam(), t.getName());
        }
        sortAll();
    }

    public MeetupTeamResponse(String name, List<TMPMeetupTeam> team) {
        this.name = name;
        for (TMPMeetupTeam t : team) {
            addMember(t.getTeam(), t.getName());
        }
        sortAll();
    }

    private void addMember(Team team, String memberName) {
        switch (team) {
            case PLANNER:
                if (planner == null) planner = new ArrayList<>();
                planner.add(memberName);
                break;
            case DESIGNER:
                if (designer == null) designer = new ArrayList<>();
                designer.add(memberName);
                break;
            case FRONTEND:
                if (frontend == null) frontend = new ArrayList<>();
                frontend.add(memberName);
                break;
            case BACKEND:
                if (backend == null) backend = new ArrayList<>();
                backend.add(memberName);
                break;
            case IOS:
                if (ios == null) ios = new ArrayList<>();
                ios.add(memberName);
                break;
            case ANDROID:
                if (aos == null) aos = new ArrayList<>();
                aos.add(memberName);
                break;
        }
    }

    private void sortAll() {
        if (planner != null) Collections.sort(planner);
        if (designer != null) Collections.sort(designer);
        if (frontend != null) Collections.sort(frontend);
        if (backend != null) Collections.sort(backend);
        if (ios != null) Collections.sort(ios);
        if (aos != null) Collections.sort(aos);
    }
}
