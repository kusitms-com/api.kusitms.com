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

    private String name; // The actual tag name, e.g., "#지도", "#글쓰기"

    @ManyToMany(mappedBy = "tags")
    private Set<CorporateProject> corporateProjects = new HashSet<>();

    @ManyToMany(mappedBy = "tags")
    private Set<MeetupProject> meetupProjects = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }
}
