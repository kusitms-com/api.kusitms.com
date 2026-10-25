package com.kusitms.website.global.auth;

import com.kusitms.website.domain.user.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {
    private Long pk;
    private String id;
    private String paswword;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long pk, String id, Collection<? extends GrantedAuthority> authorities) {
        this.pk = pk;
        this.id = id;
        this.authorities = authorities;
    }


    public static UserPrincipal create(Member user) {
        // 어드민 권한(MEMBER/MANAGER/OWNER)과 학회원 구분(YB/OB)을 각각 ROLE로 부여한다.
        // 상위 권한이 하위 권한을 포함하는 것은 SecurityConfig의 RoleHierarchy가 처리한다.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getAuthority().toRoleName()));
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }
        return new UserPrincipal(
                user.getUserId(),
                user.getId(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return paswword;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}