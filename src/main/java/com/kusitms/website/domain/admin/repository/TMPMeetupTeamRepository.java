package com.kusitms.website.domain.admin.repository;

import com.kusitms.website.domain.admin.entity.TMPMeetupProject;
import com.kusitms.website.domain.admin.entity.TMPMeetupTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TMPMeetupTeamRepository extends JpaRepository<TMPMeetupTeam, Long> {
    List<TMPMeetupTeam> findByMeetupProject(TMPMeetupProject project);
    void deleteByMeetupProject(TMPMeetupProject project);
}
