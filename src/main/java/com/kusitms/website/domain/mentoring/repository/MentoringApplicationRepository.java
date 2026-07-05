package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import com.kusitms.website.domain.mentoring.entity.MentoringApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentoringApplicationRepository extends JpaRepository<MentoringApplication, Long> {

    @Query("SELECT COUNT(a) FROM MentoringApplication a WHERE a.slot.slotId = :slotId AND a.status IN :statuses")
    int countBySlotIdAndStatusIn(@Param("slotId") Long slotId,
                                 @Param("statuses") List<ApplicationStatus> statuses);

    @Query("SELECT a.slot.slotId, COUNT(a) " +
            "FROM MentoringApplication a " +
            "WHERE a.slot.slotId IN :slotIds AND a.status IN :statuses " +
            "GROUP BY a.slot.slotId")
    List<Object[]> countBySlotIdsAndStatusIn(
            @Param("slotIds") List<Long> slotIds,
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

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM MentoringApplication a " +
            "WHERE a.status = :status " +
            "AND (a.applicant.userId = :userId OR a.slot.mentor.member.userId = :userId)")
    boolean existsByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") ApplicationStatus status);

    @EntityGraph(attributePaths = {"slot", "slot.mentor", "slot.mentor.member", "applicant"})
    Optional<MentoringApplication> findByApplicationIdAndApplicantUserId(Long applicationId, Long userId);

    @EntityGraph(attributePaths = {"slot", "slot.mentor", "slot.mentor.member"})
    List<MentoringApplication> findByApplicantUserIdAndStatusInOrderByCreatedAtDesc(
            Long userId, List<ApplicationStatus> statuses);

    @EntityGraph(attributePaths = {"slot", "slot.mentor", "slot.mentor.member", "applicant"})
    List<MentoringApplication> findBySlotMentorMemberUserIdAndStatusInOrderByCreatedAtDesc(
            Long userId, List<ApplicationStatus> statuses);
}
