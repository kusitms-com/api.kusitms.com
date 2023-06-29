package com.kusitms.website.domain.user;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Member {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String id;

    private String password;

    @Builder
    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public Member() {

    }
}
