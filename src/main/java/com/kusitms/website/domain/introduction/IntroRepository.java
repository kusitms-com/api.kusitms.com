package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroRepository extends JpaRepository<Introduction, Long> {
}
