package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT m FROM Mentor m JOIN FETCH m.member WHERE m.active = true ORDER BY FUNCTION('RANDOM')")
    List<Mentor> findRandomActiveMentors(Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Page<Mentor> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Page<Mentor> findByActiveTrueAndCategoryOrderByCreatedAtDesc(MentoringCategory category, Pageable pageable);

    Optional<Mentor> findByMemberUserId(Long userId);
}
