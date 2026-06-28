package com.kusitms.website.global.auth;

import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
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
        List<GrantedAuthority> authorities;
        if (user.getRole() == MemberRole.ADMIN) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities = Collections.emptyList();
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