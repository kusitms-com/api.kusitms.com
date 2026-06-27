package com.kusitms.website.domain.introduction.repository;

import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.PartnerLogo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerLogoRepository extends JpaRepository<PartnerLogo, Long> {
    void deleteByIntroduction(Introduction introduction);
}