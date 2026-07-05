package com.kusitms.website.domain.mentoring.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentoringSlot {

    @Id
    @Column(name = "slot_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotType slotType;

    private int maxAttendees;

    @Builder
    public MentoringSlot(Mentor mentor, LocalDate date, LocalTime startTime,
                         LocalTime endTime, SlotType slotType, int maxAttendees) {
        this.mentor = mentor;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotType = slotType;
        this.maxAttendees = maxAttendees;
    }

    public void updateSchedule(LocalTime endTime, SlotType slotType, int maxAttendees) {
        this.endTime = endTime;
        this.slotType = slotType;
        this.maxAttendees = maxAttendees;
    }
}
