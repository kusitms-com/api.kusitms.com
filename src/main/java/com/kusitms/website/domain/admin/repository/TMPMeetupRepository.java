package com.kusitms.website.domain.admin.repository;

import com.kusitms.website.domain.admin.entity.TMPMeetupProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TMPMeetupRepository extends JpaRepository<TMPMeetupProject, Long> {
    List<TMPMeetupProject> findAllByOrderByCardinalDesc();
}
