package com.kusitms.website.domain.introduction.repository;

import com.kusitms.website.domain.introduction.entity.ExpertLecture;
import com.kusitms.website.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertLectureRepository extends JpaRepository<ExpertLecture, Long> {
    void deleteByIntroduction(Introduction introduction);
}
