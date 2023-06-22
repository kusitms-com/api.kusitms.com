package com.kusitms.website.repository;

import com.kusitms.website.domain.introduction.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroRepository extends JpaRepository<Introduction, Long> {
}
