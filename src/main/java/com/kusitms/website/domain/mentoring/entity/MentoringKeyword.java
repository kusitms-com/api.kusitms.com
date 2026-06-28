package com.kusitms.website.domain.mentoring.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentoringKeyword {

    @Id
    @Column(name = "keyword_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @Column(nullable = false, unique = true)
    private String name;

    public MentoringKeyword(String name) {
        this.name = name;
    }
}
