package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.MentoringKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentoringKeywordRepository extends JpaRepository<MentoringKeyword, Long> {

    List<MentoringKeyword> findByKeywordIdIn(List<Long> keywordIds);
}
