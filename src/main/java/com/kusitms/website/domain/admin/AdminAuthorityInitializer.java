package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberAuthority;
import com.kusitms.website.domain.user.MemberRepository;
import com.kusitms.website.domain.user.MemberRole;
import com.kusitms.website.domain.user.MemberService;
import com.kusitms.website.domain.user.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 서버 기동 시 어드민 권한 데이터를 정리한다.
 * 1. 기존 role=ADMIN 회원을 authority=MANAGER로 옮기고, role은 기수 기준 YB/OB로 되돌린다.
 * 2. admin.owner-ids에 지정된 로그인 ID를 OWNER로 승격한다. (최초 OWNER 부트스트랩용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthorityInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Value("${admin.owner-ids:}")
    private List<String> ownerIds;

    @Override
    @Transactional
    @SuppressWarnings("deprecation")
    public void run(ApplicationArguments args) {
        for (Member member : memberRepository.findAllByRole(MemberRole.ADMIN)) {
            if (member.getAuthority() == MemberAuthority.MEMBER) {
                member.updateAuthority(MemberAuthority.MANAGER);
            }
            member.updateRole(memberService.resolveRole(member));
            log.info("레거시 ADMIN 회원 권한 이전: userId={}, authority={}", member.getUserId(), member.getAuthority());
        }

        if (ownerIds.isEmpty()) {
            return;
        }
        for (Member member : memberRepository.findAllByIdIn(ownerIds)) {
            if (member.getStatus() != MemberStatus.APPROVED) {
                log.warn("OWNER 지정 실패 (승인되지 않은 회원): id={}", member.getId());
                continue;
            }
            if (member.getAuthority() != MemberAuthority.OWNER) {
                member.updateAuthority(MemberAuthority.OWNER);
                log.info("OWNER 권한 부여: id={}", member.getId());
            }
        }
    }
}
