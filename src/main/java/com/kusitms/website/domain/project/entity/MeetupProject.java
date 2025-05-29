package com.kusitms.website.domain.project.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetupProject {
    @Id
    @Column(name = "meetup_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetupId;

    @Column(nullable = false)
    private int cardinal;

    private String name;

    @Column(length = 400)
    private String intro;

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    @Column(name = "one_line_intro", nullable = false, length = 33)
    private String oneLineIntro;

    @Column(name = "logo_url", nullable = false)
    private String logoUrl;

    @Column(name = "poster_url", nullable = false)
    private String posterUrl;

    @Column(name = "web_thumbnail_url")
    private String webThumbnailUrl;

    @Column(name = "behance_url")
    private String behanceUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "app_url")
    private String appUrl;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @OneToMany(mappedBy = "meetupProject", cascade = CascadeType.ALL)
    private List<MeetupTeam> team = new ArrayList<>();

    @Column(name = "display_flag")
    private boolean displayFlag;

    @ManyToMany
    @JoinTable(
            name = "meetup_project_tags",
            joinColumns = @JoinColumn(name = "meetup_project_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Builder
    public MeetupProject(int cardinal, String name, String intro, ProjectType type, String oneLineIntro,
                         String logoUrl, String posterUrl, String webThumbnailUrl, String behanceUrl, String instagramUrl, String githubUrl, String appUrl,
                         Boolean displayFlag, LocalDate startDate, LocalDate endDate, String teamName) {
        this.cardinal = cardinal;
        this.name = name;
        this.intro = intro;
        this.type = type;
        this.displayFlag = displayFlag;
        this.oneLineIntro = oneLineIntro;
        this.logoUrl = logoUrl;
        this.posterUrl = posterUrl;
        this.webThumbnailUrl = webThumbnailUrl;
        this.behanceUrl = behanceUrl;
        this.instagramUrl = instagramUrl;
        this.githubUrl = githubUrl;
        this.appUrl = appUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.teamName = teamName;
    }

    public void updateTeam(List<MeetupTeam> team) {
        this.team = team;
    }
}
