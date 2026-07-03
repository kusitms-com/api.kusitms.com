package com.kusitms.website.domain.user;

import com.kusitms.website.domain.user.dto.response.AccountProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {

    private final MemberRepository memberRepository;

    public AccountProfileResponse getAccountProfile(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return AccountProfileResponse.from(member);
    }
}
