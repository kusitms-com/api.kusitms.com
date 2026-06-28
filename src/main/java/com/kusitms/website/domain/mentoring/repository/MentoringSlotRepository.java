package com.kusitms.website.domain.mentoring.repository;

import com.kusitms.website.domain.mentoring.entity.MentoringSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MentoringSlotRepository extends JpaRepository<MentoringSlot, Long> {

    List<MentoringSlot> findByMentorMentorIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
            Long mentorId, LocalDate fromDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM MentoringSlot s WHERE s.slotId = :slotId")
    Optional<MentoringSlot> findByIdWithLock(@Param("slotId") Long slotId);
}
