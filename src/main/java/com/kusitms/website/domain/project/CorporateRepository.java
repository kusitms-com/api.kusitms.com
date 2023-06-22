package com.kusitms.website.domain.project;

import com.kusitms.website.domain.project.entity.CorporateProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorporateRepository extends JpaRepository<CorporateProject, Long> {
    List<CorporateProject> findAllByOrderByCardinalDesc();
}
