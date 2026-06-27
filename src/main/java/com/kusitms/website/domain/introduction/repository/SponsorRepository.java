package com.kusitms.website.domain.introduction.repository;

import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    void deleteByIntroduction(Introduction introduction);
}