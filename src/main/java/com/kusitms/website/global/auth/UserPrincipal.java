package com.kusitms.website.global.auth;

import com.kusitms.website.domain.user.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserPrincipal implements UserDetails {
    private Long pk;
    private String id;
    private String paswword;

    public UserPrincipal(Long pk, String id) {
        this.pk = pk;
        this.id = id;
    }


    public static UserPrincipal create(Member user) {
        return new UserPrincipal(
                user.getUserId(),
                user.getId()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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