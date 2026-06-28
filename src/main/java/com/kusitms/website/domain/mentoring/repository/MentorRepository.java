package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT m FROM Mentor m WHERE m.active = true ORDER BY FUNCTION('RANDOM')")
    List<Mentor> findRandomActiveMentors(Pageable pageable);

    Page<Mentor> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Mentor> findByActiveTrueAndCategoryOrderByCreatedAtDesc(MentoringCategory category, Pageable pageable);
}
