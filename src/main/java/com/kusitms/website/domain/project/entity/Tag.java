package com.kusitms.website.domain.project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<CorporateProject> corporateProjects = new HashSet<>();

    @ManyToMany(mappedBy = "tags")
    private Set<MeetupProject> meetupProjects = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }
}
