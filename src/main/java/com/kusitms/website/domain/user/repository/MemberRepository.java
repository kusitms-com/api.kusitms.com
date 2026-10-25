package com.kusitms.website.domain.user.repository;

import com.kusitms.website.domain.user.entity.Member;
import com.kusitms.website.domain.user.entity.MemberAuthority;
import com.kusitms.website.domain.user.entity.MemberRole;
import com.kusitms.website.domain.user.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndStatus(String id, MemberStatus status);

    Optional<Member> findById(String id);

    boolean existsById(String id);

    List<Member> findAllByStatus(MemberStatus status);

    Optional<Member> findByRefreshToken(String refreshToken);

    List<Member> findAllByRole(MemberRole role);

    List<Member> findAllByIdIn(List<String> ids);

    List<Member> findAllByOrderByCreatedAtDesc();

    // OWNER 권한 변경 시 동시 강등으로 OWNER가 0명이 되는 것을 막기 위해 행 잠금을 건다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findAllByAuthority(MemberAuthority authority);
}
