package com.kusitms.website.domain.blog.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class BlogAuthor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_author_id", nullable = false)
    private Long id;

    @Column(length = 20)
    private String name;

    private Integer generation;

    @Enumerated(EnumType.STRING)
    private Position position;

}
