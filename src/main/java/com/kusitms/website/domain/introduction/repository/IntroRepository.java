package com.kusitms.website.domain.introduction.repository;

import com.kusitms.website.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntroRepository extends JpaRepository<Introduction, Long> {
    Optional<Introduction> findFirstByOrderByIntroductionIdAsc();
}
