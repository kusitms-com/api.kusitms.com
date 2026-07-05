package com.kusitms.website.domain.user;

import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import com.kusitms.website.domain.mentoring.entity.MentoringApplication;
import com.kusitms.website.domain.mentoring.repository.MentorRepository;
import com.kusitms.website.domain.mentoring.repository.MentoringApplicationRepository;
import com.kusitms.website.domain.mentoring.repository.MentoringReviewRepository;
import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.user.dto.request.AccountProfileUpdateRequest;
import com.kusitms.website.domain.user.dto.request.PasswordChangeRequest;
import com.kusitms.website.domain.user.dto.response.AccountProfileResponse;
import com.kusitms.website.domain.user.dto.response.ApplicationRejectionReasonResponse;
import com.kusitms.website.domain.user.dto.response.MyMentoringCardResponse;
import com.kusitms.website.domain.user.dto.response.YBMypageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^010\\d{8}$");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    private final MemberRepository memberRepository;
    private final MentoringApplicationRepository mentoringApplicationRepository;
    private final MentoringReviewRepository mentoringReviewRepository;
    private final MentorRepository mentorRepository;
    private final S3Service s3Service;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final List<ApplicationStatus> PENDING_STATUSES =
            Collections.singletonList(ApplicationStatus.PENDING);
    private static final List<ApplicationStatus> ACTIVE_STATUSES =
            Collections.singletonList(ApplicationStatus.ACTIVE);
    private static final List<ApplicationStatus> FINISHED_STATUSES =
            Arrays.asList(ApplicationStatus.COMPLETED, ApplicationStatus.REJECTED);

    public YBMypageResponse getYBMypage(Long userId) {
        Member member = getActiveMember(userId);

        List<MyMentoringCardResponse> pendingMentorings = mentoringApplicationRepository
                .findByApplicantUserIdAndStatusInOrderByCreatedAtDesc(userId, PENDING_STATUSES)
                .stream()
                .map(application -> MyMentoringCardResponse.from(application, false))
                .collect(Collectors.toList());

        List<MyMentoringCardResponse> activeMentorings = mentoringApplicationRepository
                .findByApplicantUserIdAndStatusInOrderByCreatedAtDesc(userId, ACTIVE_STATUSES)
                .stream()
                .map(application -> MyMentoringCardResponse.from(application, false))
                .collect(Collectors.toList());

        List<MyMentoringCardResponse> completedMentorings = mentoringApplicationRepository
                .findByApplicantUserIdAndStatusInOrderByCreatedAtDesc(userId, FINISHED_STATUSES)
                .stream()
                .map(application -> MyMentoringCardResponse.from(
                        application,
                        application.getStatus() == ApplicationStatus.COMPLETED
                                && !mentoringReviewRepository.existsByApplicationApplicationId(application.getApplicationId())
                ))
                .collect(Collectors.toList());

        return YBMypageResponse.builder()
                .profile(AccountProfileResponse.from(member))
                .pendingMentorings(pendingMentorings)
                .activeMentorings(activeMentorings)
                .completedMentorings(completedMentorings)
                .build();
    }

    public ApplicationRejectionReasonResponse getApplicationRejectionReason(Long userId, Long applicationId) {
        MentoringApplication application = mentoringApplicationRepository
                .findByApplicationIdAndApplicantUserId(applicationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토링 신청입니다."));

        if (application.getStatus() != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException("거절된 멘토링 신청만 조회할 수 있습니다.");
        }

        return new ApplicationRejectionReasonResponse(
                application.getApplicationId(),
                application.getRejectionReason()
        );
    }

    public AccountProfileResponse getAccountProfile(Long userId) {
        Member member = getActiveMember(userId);
        return AccountProfileResponse.from(member);
    }

    @Transactional
    public AccountProfileResponse updateAccountProfile(
            Long userId,
            AccountProfileUpdateRequest request,
            MultipartFile profileImage
    ) {
        Member member = getActiveMember(userId);

        String normalizedPhone = normalizePhone(request.getPhone());
        if (!PHONE_PATTERN.matcher(normalizedPhone).matches()) {
            throw new IllegalArgumentException("올바른 전화번호를 입력해 주세요.");
        }

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            validateImageFile(profileImage);
            profileImageUrl = s3Service.uploadFile(profileImage, "profile");
        }

        member.updateAccountProfile(request.getName(), normalizedPhone, profileImageUrl);
        return AccountProfileResponse.from(member);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        Member member = getActiveMember(userId);

        if (!bCryptPasswordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }
        if (!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            throw new IllegalArgumentException("영문, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void withdrawAccount(Long userId) {
        Member member = getActiveMember(userId);

        boolean hasActiveMentoring = mentoringApplicationRepository
                .existsByUserIdAndStatus(userId, ApplicationStatus.ACTIVE);
        if (hasActiveMentoring) {
            throw new IllegalArgumentException("진행 중인 멘토링이 있어 탈퇴할 수 없습니다. 멘토링 완료 후 다시 시도해 주세요.");
        }

        mentorRepository.findByMemberUserId(userId)
                .ifPresent(mentor -> mentor.deactivate());

        member.withdraw();
    }

    private Member getActiveMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalArgumentException("이미 탈퇴한 계정입니다.");
        }
        return member;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("10MB 이하의 이미지만 업로드 가능합니다.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("이미지 파일(jpg, png)만 업로드 가능합니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("이미지 파일(jpg, png)만 업로드 가능합니다.");
        }
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }
}
