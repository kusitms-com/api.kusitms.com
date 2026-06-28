package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.dto.response.PendingMemberResponse;
import com.kusitms.website.domain.email.service.MailService;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRepository;
import com.kusitms.website.domain.user.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;
    private final MailService mailService;

    public List<PendingMemberResponse> getPendingMembers() {
        return memberRepository.findAllByStatus(MemberStatus.PENDING).stream()
                .map(PendingMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new IllegalArgumentException("승인 대기 상태가 아닌 회원입니다.");
        }

        member.approve();

        if (member.getEmail() != null && !member.getEmail().isBlank()) {
            mailService.sendApprovalEmail(member.getEmail(), member.getName());
        }
    }

    @Transactional
    public void rejectMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new IllegalArgumentException("승인 대기 상태가 아닌 회원입니다.");
        }

        String name = member.getName();
        String email = member.getEmail();
        memberRepository.delete(member);

        if (email != null && !email.isBlank()) {
            mailService.sendRejectionEmail(email, name);
        }
    }
}
