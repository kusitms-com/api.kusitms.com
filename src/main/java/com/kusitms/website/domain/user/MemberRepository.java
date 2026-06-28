package com.kusitms.website.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndStatus(String id, MemberStatus status);

    Optional<Member> findById(String id);

    boolean existsById(String id);

    List<Member> findAllByStatus(MemberStatus status);

    Optional<Member> findByRefreshToken(String refreshToken);
}
