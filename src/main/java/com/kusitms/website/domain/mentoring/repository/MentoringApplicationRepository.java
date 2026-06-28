package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import com.kusitms.website.domain.mentoring.entity.MentoringApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentoringApplicationRepository extends JpaRepository<MentoringApplication, Long> {

    @Query("SELECT COUNT(a) FROM MentoringApplication a WHERE a.slot.slotId = :slotId AND a.status IN :statuses")
    int countBySlotIdAndStatusIn(@Param("slotId") Long slotId,
                                 @Param("statuses") List<ApplicationStatus> statuses);

    boolean existsBySlotSlotIdAndApplicantUserIdAndStatusIn(
            Long slotId, Long userId, List<ApplicationStatus> statuses);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM MentoringApplication a " +
            "WHERE a.slot.mentor.mentorId = :mentorId " +
            "AND a.applicant.userId = :userId " +
            "AND a.status IN :statuses")
    boolean existsByMentorIdAndApplicantIdAndStatusIn(
            @Param("mentorId") Long mentorId,
            @Param("userId") Long userId,
            @Param("statuses") List<ApplicationStatus> statuses);
}
