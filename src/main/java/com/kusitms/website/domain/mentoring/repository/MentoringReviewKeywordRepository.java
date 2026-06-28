package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.MentoringReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentoringReviewKeywordRepository extends JpaRepository<MentoringReviewKeyword, Long> {

    @Query("SELECT rk.keyword.keywordId, rk.keyword.name, COUNT(rk) " +
            "FROM MentoringReviewKeyword rk " +
            "WHERE rk.review.mentor.mentorId = :mentorId " +
            "GROUP BY rk.keyword.keywordId, rk.keyword.name " +
            "HAVING COUNT(rk) >= 3 " +
            "ORDER BY COUNT(rk) DESC, rk.keyword.keywordId ASC")
    List<Object[]> findKeywordStatsForMentor(@Param("mentorId") Long mentorId);

    @Query("SELECT rk.review.mentor.mentorId, rk.keyword.name " +
            "FROM MentoringReviewKeyword rk " +
            "WHERE rk.review.mentor.mentorId IN :mentorIds " +
            "GROUP BY rk.review.mentor.mentorId, rk.keyword.keywordId, rk.keyword.name " +
            "HAVING COUNT(rk) >= 3 " +
            "ORDER BY rk.review.mentor.mentorId, COUNT(rk) DESC, rk.keyword.keywordId ASC")
    List<Object[]> findTopKeywordsForMentors(@Param("mentorIds") List<Long> mentorIds);
}
