package com.kusitms.website.domain.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String id;

    private String password;

    private String name;

    private String phone;

    private Integer cardinal;

    @Enumerated(EnumType.STRING)
    private Part part;

    private String profileImageUrl;

    private String certificateImageUrl;

    private String email;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole role;

    private String refreshToken;

    private LocalDateTime createdAt;

    @Builder
    public Member(String id, String password, String name, String phone,
                  Integer cardinal, Part part, String profileImageUrl,
                  String certificateImageUrl, String email, MemberStatus status, MemberRole role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.cardinal = cardinal;
        this.part = part;
        this.profileImageUrl = profileImageUrl;
        this.certificateImageUrl = certificateImageUrl;
        this.email = email;
        this.status = status;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }

    public void updateAccountProfile(String name, String phone, String profileImageUrl) {
        this.name = name;
        this.phone = phone;
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void withdraw() {
        String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        this.id = "withdrawn_" + this.userId + "_" + suffix;
        this.password = null;
        this.name = "탈퇴한 회원";
        this.phone = null;
        this.part = null;
        this.profileImageUrl = null;
        this.certificateImageUrl = null;
        this.email = null;
        this.role = null;
        this.refreshToken = null;
        this.status = MemberStatus.WITHDRAWN;
    }

    public void approve() {
        this.status = MemberStatus.APPROVED;
    }
}
