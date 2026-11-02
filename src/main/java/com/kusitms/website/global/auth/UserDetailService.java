package com.kusitms.website.global.auth;

import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRepository;
import org.springframework.security.authentication.DisabledException;
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
        Member user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        // 권한은 토큰이 아니라 매 요청마다 DB에서 읽으므로, 탈퇴/권한 변경이 즉시 반영된다.
        if (!user.isActive()) {
            throw new DisabledException("승인되지 않았거나 탈퇴한 회원입니다.");
        }

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        return userPrincipal;
    }
}
