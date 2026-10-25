package com.kusitms.website.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentCardinalRepository extends JpaRepository<CurrentCardinal, Long> {
    // 현재 기수는 단일 행으로 관리한다
    Optional<CurrentCardinal> findTopByOrderByIdAsc();
}
