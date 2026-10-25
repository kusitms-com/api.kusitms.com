package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.dto.response.CurrentCardinalUpdateResponse;
import com.kusitms.website.domain.user.CurrentCardinal;
import com.kusitms.website.domain.user.CurrentCardinalRepository;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRepository;
import com.kusitms.website.domain.user.MemberRole;
import com.kusitms.website.domain.user.MemberService;
import com.kusitms.website.domain.user.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardinalAdminService {

    private final CurrentCardinalRepository currentCardinalRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    /**
     * 현재 기수를 변경하고, 기수 기준 YB/OB를 전체 회원에 바로 반영한다.
     * 로그인 시 재계산만으로는 로그인하지 않은 회원이 이전 역할로 남기 때문이다.
     */
    @Transactional
    public CurrentCardinalUpdateResponse updateCurrentCardinal(Integer cardinal) {
        if (cardinal == null || cardinal < 1) {
            throw new IllegalArgumentException("변경할 기수를 올바르게 입력해 주세요.");
        }

        CurrentCardinal currentCardinal = currentCardinalRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("현재 기수가 설정되지 않았습니다."));
        Integer previousCardinal = currentCardinal.getCardinal();
        currentCardinal.updateCardinal(cardinal);

        int changedToOb = 0;
        int changedToYb = 0;
        for (Member member : memberRepository.findAll()) {
            if (member.getStatus() == MemberStatus.WITHDRAWN || member.getCardinal() == null) {
                continue;
            }
            MemberRole role = memberService.resolveRole(member, cardinal);
            if (member.getRole() == role) {
                continue;
            }
            if (role == MemberRole.OB) changedToOb++;
            else changedToYb++;
            member.updateRole(role);
        }

        log.info("현재 기수 변경: {} -> {}, YB->OB {}명, OB->YB {}명", previousCardinal, cardinal, changedToOb, changedToYb);
        return new CurrentCardinalUpdateResponse(previousCardinal, cardinal, changedToOb, changedToYb);
    }
}
