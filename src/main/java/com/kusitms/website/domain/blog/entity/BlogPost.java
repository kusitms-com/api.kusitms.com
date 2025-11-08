    package com.kusitms.website.domain.blog.entity;

    import lombok.Getter;

    import javax.persistence.*;
    import java.time.LocalDateTime;

    @Entity
    @Getter
    public class BlogPost {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "blog_post_id", nullable = false)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "blog_author_id")
        private BlogAuthor blogAuthor;

        @Column(length = 100)
        private String title;

        @Enumerated(EnumType.STRING)
        private Category category;

        @Column(length = 512)
        private String address;

        @Column(length = 512)
        private String imageAddress;

        @Column(columnDefinition = "TEXT")
        private String content;

    }
