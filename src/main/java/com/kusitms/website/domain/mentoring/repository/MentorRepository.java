package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT m FROM Mentor m JOIN FETCH m.member WHERE m.active = true ORDER BY FUNCTION('RANDOM')")
    List<Mentor> findRandomActiveMentors(Pageable pageable);

    @Query("SELECT m FROM Mentor m JOIN FETCH m.member WHERE m.active = true ORDER BY m.createdAt DESC")
    Page<Mentor> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT m FROM Mentor m JOIN FETCH m.member WHERE m.active = true AND m.category = :category ORDER BY m.createdAt DESC")
    Page<Mentor> findByActiveTrueAndCategoryOrderByCreatedAtDesc(@Param("category") MentoringCategory category, Pageable pageable);
}
