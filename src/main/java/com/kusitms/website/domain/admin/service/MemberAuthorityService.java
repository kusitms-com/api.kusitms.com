package com.kusitms.website.domain.admin.service;

import com.kusitms.website.domain.user.entity.Member;
import com.kusitms.website.domain.user.entity.MemberAuthority;
import com.kusitms.website.domain.user.repository.MemberRepository;
import com.kusitms.website.domain.user.entity.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAuthorityService {

    private final MemberRepository memberRepository;

    @Transactional
    public void changeAuthority(Long actorUserId, Long targetUserId, MemberAuthority newAuthority) {
        if (newAuthority == null) {
            throw new IllegalArgumentException("변경할 권한을 입력해 주세요.");
        }
        if (actorUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신의 권한은 변경할 수 없습니다.");
        }

        // OWNER 행을 먼저 잠가 두 OWNER가 서로를 동시에 강등하는 경우에도 OWNER가 0명이 되지 않게 한다.
        List<Member> owners = memberRepository.findAllByAuthority(MemberAuthority.OWNER);

        Member target = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (target.getStatus() != MemberStatus.APPROVED) {
            throw new IllegalArgumentException("가입 승인된 회원에게만 권한을 부여할 수 있습니다.");
        }
        if (target.getAuthority() == newAuthority) {
            return;
        }
        if (target.getAuthority() == MemberAuthority.OWNER && owners.size() <= 1) {
            throw new IllegalArgumentException("마지막 OWNER는 강등할 수 없습니다.");
        }

        target.updateAuthority(newAuthority);
    }
}
