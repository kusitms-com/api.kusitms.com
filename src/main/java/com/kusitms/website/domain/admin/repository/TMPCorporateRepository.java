package com.kusitms.website.domain.admin.repository;

import com.kusitms.website.domain.admin.entity.TMPCorporateProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TMPCorporateRepository extends JpaRepository<TMPCorporateProject, Long> {
    List<TMPCorporateProject> findAllByOrderByCardinalDesc();

}
