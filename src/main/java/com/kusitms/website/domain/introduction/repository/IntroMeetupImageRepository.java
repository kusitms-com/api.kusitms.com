package com.kusitms.website.domain.introduction.repository;

import com.kusitms.website.domain.introduction.entity.IntroMeetupImage;
import com.kusitms.website.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroMeetupImageRepository extends JpaRepository<IntroMeetupImage, Long> {
    void deleteByIntroduction(Introduction introduction);
}