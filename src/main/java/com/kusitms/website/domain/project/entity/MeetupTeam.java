package com.kusitms.website.domain.project.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetupTeam {
    @Id
    @Column(name = "team_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetup_id")
    private MeetupProject meetupProject;

    @Enumerated(EnumType.STRING)
    private Team team;

    private String name;

    @Builder
    public MeetupTeam(MeetupProject meetup, Team team, String name) {
        this.meetupProject = meetup;
        meetup.getTeam().add(this);
        this.team = team;
        this.name = name;
    }
}
