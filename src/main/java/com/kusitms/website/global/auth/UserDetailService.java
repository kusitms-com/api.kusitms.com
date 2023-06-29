package com.kusitms.website.global.auth;

import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final MemberRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member user = userRepository.findById(Long.parseLong(userId)).orElseThrow();

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        return userPrincipal;
    }
}
