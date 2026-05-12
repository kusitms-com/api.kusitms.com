package com.kusitms.website.domain.introduction.repository;

import com.kusitms.website.domain.introduction.entity.Activity;
import com.kusitms.website.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    void deleteByIntroduction(Introduction introduction);
}