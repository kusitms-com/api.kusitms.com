package com.kusitms.website.domain.user;

import com.kusitms.website.domain.mentoring.entity.ApplicationStatus;
import com.kusitms.website.domain.mentoring.entity.MentoringKeyword;
import com.kusitms.website.domain.mentoring.entity.MentoringApplication;
import com.kusitms.website.domain.mentoring.entity.MentoringReview;
import com.kusitms.website.domain.mentoring.entity.MentoringReviewKeyword;
import com.kusitms.website.domain.mentoring.entity.Mentor;
import com.kusitms.website.domain.mentoring.entity.MentoringSlot;
import com.kusitms.website.domain.mentoring.entity.SlotType;
import com.kusitms.website.domain.mentoring.repository.MentorRepository;
import com.kusitms.website.domain.mentoring.repository.MentoringApplicationRepository;
import com.kusitms.website.domain.mentoring.repository.MentoringKeywordRepository;
import com.kusitms.website.domain.mentoring.repository.MentoringReviewRepository;
import com.kusitms.website.domain.mentoring.repository.MentoringSlotRepository;
import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.user.dto.request.AccountProfileUpdateRequest;
import com.kusitms.website.domain.user.dto.request.ApplicationRejectRequest;
import com.kusitms.website.domain.user.dto.request.MentoringReviewCreateRequest;
import com.kusitms.website.domain.user.dto.request.OBProfileUpdateRequest;
import com.kusitms.website.domain.user.dto.request.OBProfileVisibilityUpdateRequest;
import com.kusitms.website.domain.user.dto.request.OBScheduleUpdateRequest;
import com.kusitms.website.domain.user.dto.request.PasswordChangeRequest;
import com.kusitms.website.domain.user.dto.response.AccountProfileResponse;
import com.kusitms.website.domain.user.dto.response.ApplicationRejectionReasonResponse;
import com.kusitms.website.domain.user.dto.response.OBMentoringRequestCardResponse;
import com.kusitms.website.domain.user.dto.response.OBMentoringRequestsResponse;
import com.kusitms.website.domain.user.dto.response.OBScheduleDateResponse;
import com.kusitms.website.domain.user.dto.response.OBScheduleResponse;
import com.kusitms.website.domain.user.dto.response.MyMentoringCardResponse;
import com.kusitms.website.domain.user.dto.response.OBProfileResponse;
import com.kusitms.website.domain.user.dto.response.YBMypageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final MentoringKeywordRepository mentoringKeywordRepository;
    private final MentoringReviewRepository mentoringReviewRepository;
    private final MentorRepository mentorRepository;
    private final MentoringSlotRepository mentoringSlotRepository;
    private final S3Service s3Service;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final List<ApplicationStatus> PENDING_STATUSES =
            Collections.singletonList(ApplicationStatus.PENDING);
    private static final List<ApplicationStatus> ACTIVE_STATUSES =
            Collections.singletonList(ApplicationStatus.ACTIVE);
    private static final List<ApplicationStatus> FINISHED_STATUSES =
            Arrays.asList(ApplicationStatus.COMPLETED, ApplicationStatus.REJECTED);
    private static final List<ApplicationStatus> OCCUPYING_STATUSES =
            Arrays.asList(ApplicationStatus.PENDING, ApplicationStatus.ACTIVE);
    private static final LocalTime SLOT_START_MIN_TIME = LocalTime.of(9, 0);

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

    @Transactional
    public void createMentoringReview(Long userId, MentoringReviewCreateRequest request) {
        MentoringApplication application = mentoringApplicationRepository
                .findByApplicationIdAndApplicantUserId(request.getApplicationId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토링 신청입니다."));

        if (application.getStatus() != ApplicationStatus.COMPLETED) {
            throw new IllegalArgumentException("완료된 멘토링에 한해서만 후기를 작성할 수 있습니다.");
        }
        if (mentoringReviewRepository.existsByApplicationApplicationId(application.getApplicationId())) {
            throw new IllegalArgumentException("이미 후기가 작성된 멘토링입니다.");
        }

        List<Long> keywordIds = request.getKeywordIds();
        long distinctKeywordCount = keywordIds.stream().distinct().count();
        if (distinctKeywordCount != keywordIds.size()) {
            throw new IllegalArgumentException("중복된 키워드는 선택할 수 없습니다.");
        }

        List<MentoringKeyword> keywords = mentoringKeywordRepository.findByKeywordIdIn(keywordIds);
        if (keywords.size() != keywordIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 키워드가 포함되어 있습니다.");
        }

        MentoringReview review = MentoringReview.builder()
                .mentor(application.getSlot().getMentor())
                .reviewer(application.getApplicant())
                .application(application)
                .content(request.getContent())
                .recommendationType(request.getRecommendationType())
                .build();

        for (MentoringKeyword keyword : keywords) {
            review.addKeyword(new MentoringReviewKeyword(review, keyword));
        }

        mentoringReviewRepository.save(review);
    }

    public AccountProfileResponse getAccountProfile(Long userId) {
        Member member = getActiveMember(userId);
        return AccountProfileResponse.from(member);
    }

    public OBProfileResponse getOBProfile(Long userId) {
        Member member = getOBMember(userId);
        Mentor mentor = mentorRepository.findByMemberUserId(userId).orElse(null);
        return OBProfileResponse.from(member, mentor);
    }

    public OBScheduleResponse getOBSchedule(Long userId) {
        Mentor mentor = getExistingMentor(userId);

        List<MentoringSlot> slots = mentoringSlotRepository
                .findByMentorMentorIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
                        mentor.getMentorId(), LocalDate.now());

        Map<Long, Integer> applicantCountMap = getApplicantCountMap(slots);
        Map<LocalDate, List<MentoringSlot>> slotsByDate = slots.stream()
                .collect(Collectors.groupingBy(
                        MentoringSlot::getDate,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<OBScheduleDateResponse> schedules = slotsByDate.entrySet().stream()
                .map(entry -> OBScheduleDateResponse.from(entry.getKey(), entry.getValue(), applicantCountMap))
                .collect(Collectors.toList());

        return OBScheduleResponse.builder()
                .schedules(schedules)
                .build();
    }

    public OBMentoringRequestsResponse getOBMentoringRequests(Long userId, int page) {
        getExistingMentor(userId);

        Page<MentoringApplication> requestPage = mentoringApplicationRepository
                .findBySlotMentorMemberUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, 10));

        List<OBMentoringRequestCardResponse> pendingRequests = requestPage.getContent().stream()
                .filter(application -> application.getStatus() == ApplicationStatus.PENDING)
                .map(OBMentoringRequestCardResponse::from)
                .collect(Collectors.toList());

        List<OBMentoringRequestCardResponse> activeRequests = requestPage.getContent().stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACTIVE)
                .map(OBMentoringRequestCardResponse::from)
                .collect(Collectors.toList());

        List<OBMentoringRequestCardResponse> completedRequests = requestPage.getContent().stream()
                .filter(application -> application.getStatus() == ApplicationStatus.COMPLETED)
                .map(OBMentoringRequestCardResponse::from)
                .collect(Collectors.toList());

        List<OBMentoringRequestCardResponse> rejectedRequests = requestPage.getContent().stream()
                .filter(application -> application.getStatus() == ApplicationStatus.REJECTED)
                .map(OBMentoringRequestCardResponse::from)
                .collect(Collectors.toList());

        return OBMentoringRequestsResponse.builder()
                .totalCount(requestPage.getTotalElements())
                .totalPages(requestPage.getTotalPages())
                .currentPage(requestPage.getNumber())
                .pendingRequests(pendingRequests)
                .activeRequests(activeRequests)
                .completedRequests(completedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Transactional
    public OBProfileResponse updateOBProfile(
            Long userId,
            OBProfileUpdateRequest request,
            MultipartFile mentorProfileImage
    ) {
        Member member = getOBMember(userId);
        Mentor mentor = mentorRepository.findByMemberUserId(userId)
                .orElseGet(() -> mentorRepository.save(Mentor.builder()
                        .member(member)
                        .title(request.getTitle())
                        .profileImageUrl(null)
                        .category(request.getCategory())
                        .experience(request.getExperience())
                        .method(request.getMethod())
                        .durationMinutes(request.getDurationMinutes())
                        .pricePerHour(request.getPricePerHour())
                        .introduction(request.getIntroduction())
                        .acceptingRequests(false)
                        .active(false)
                        .build()));

        String mentorProfileImageUrl = null;
        if (mentorProfileImage != null && !mentorProfileImage.isEmpty()) {
            validateImageFile(mentorProfileImage);
            mentorProfileImageUrl = s3Service.uploadFile(mentorProfileImage, "mentor-profile");
        }

        mentor.updateProfile(
                request.getTitle(),
                mentorProfileImageUrl,
                request.getCategory(),
                request.getExperience(),
                request.getMethod(),
                request.getDurationMinutes(),
                request.getPricePerHour(),
                request.getIntroduction()
        );
        mentor.updateVisibility(calculateMentorVisibility(mentor));

        return OBProfileResponse.from(member, mentor);
    }

    @Transactional
    public OBProfileResponse updateOBProfileVisibility(Long userId, OBProfileVisibilityUpdateRequest request) {
        Member member = getOBMember(userId);
        Mentor mentor = getExistingMentor(userId);

        mentor.updateAcceptingRequests(request.getEnabled());
        mentor.updateVisibility(calculateMentorVisibility(mentor));

        return OBProfileResponse.from(member, mentor);
    }

    @Transactional
    public OBScheduleResponse updateOBSchedule(Long userId, OBScheduleUpdateRequest request) {
        Mentor mentor = getExistingMentor(userId);

        validateScheduleRequest(mentor, request);

        LocalDate date = request.getDate();
        SlotType slotType = request.getGroupMentoring() ? SlotType.ONE_TO_N : SlotType.ONE_TO_ONE;
        int maxAttendees = request.getGroupMentoring() ? request.getMaxAttendees() : 1;

        List<LocalTime> sortedStartTimes = request.getStartTimes().stream()
                .sorted()
                .collect(Collectors.toList());

        List<MentoringSlot> existingSlots = mentoringSlotRepository
                .findByMentorMentorIdAndDateWithLock(mentor.getMentorId(), date);

        Map<Long, Integer> applicantCountMap = getApplicantCountMap(existingSlots);
        Map<LocalTime, MentoringSlot> existingSlotMap = existingSlots.stream()
                .collect(Collectors.toMap(MentoringSlot::getStartTime, slot -> slot));
        Set<LocalTime> requestedStartTimes = Set.copyOf(sortedStartTimes);

        validateLockedSlots(existingSlots, applicantCountMap, requestedStartTimes, slotType, maxAttendees);

        for (MentoringSlot existingSlot : existingSlots) {
            if (applicantCountMap.getOrDefault(existingSlot.getSlotId(), 0) > 0) {
                continue;
            }

            if (!requestedStartTimes.contains(existingSlot.getStartTime())) {
                mentoringSlotRepository.delete(existingSlot);
                continue;
            }

            existingSlot.updateSchedule(
                    calculateEndTime(existingSlot.getStartTime(), mentor.getDurationMinutes()),
                    slotType,
                    maxAttendees
            );
        }

        for (LocalTime startTime : sortedStartTimes) {
            if (existingSlotMap.containsKey(startTime)) {
                continue;
            }

            mentoringSlotRepository.save(MentoringSlot.builder()
                    .mentor(mentor)
                    .date(date)
                    .startTime(startTime)
                    .endTime(calculateEndTime(startTime, mentor.getDurationMinutes()))
                    .slotType(slotType)
                    .maxAttendees(maxAttendees)
                    .build());
        }

        mentor.updateVisibility(calculateMentorVisibility(mentor));

        return getOBSchedule(userId);
    }

    @Transactional
    public void rejectOBMentoringRequest(Long userId, Long applicationId, ApplicationRejectRequest request) {
        MentoringApplication application = mentoringApplicationRepository
                .findByApplicationIdAndSlotMentorMemberUserId(applicationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토링 신청입니다."));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 멘토링 신청만 거절할 수 있습니다.");
        }

        application.reject(request.getRejectionReason().trim());
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

    private Member getOBMember(Long userId) {
        Member member = getActiveMember(userId);
        if (member.getRole() != MemberRole.OB) {
            throw new IllegalArgumentException("OB 회원만 접근할 수 있습니다.");
        }
        return member;
    }

    private Mentor getExistingMentor(Long userId) {
        getOBMember(userId);
        return mentorRepository.findByMemberUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("멘토 프로필을 먼저 작성해 주세요."));
    }

    private boolean calculateMentorVisibility(Mentor mentor) {
        if (!mentor.isAcceptingRequests()) {
            return false;
        }
        if (!mentor.isProfileCompleted()) {
            return false;
        }
        return mentoringSlotRepository.existsByMentorMentorIdAndDateGreaterThanEqual(
                mentor.getMentorId(), LocalDate.now());
    }

    private void validateScheduleRequest(Mentor mentor, OBScheduleUpdateRequest request) {
        if (mentor.getDurationMinutes() == null) {
            throw new IllegalArgumentException("멘토링 한타임 시간을 먼저 설정해 주세요.");
        }
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("오늘 이후 날짜만 등록할 수 있습니다.");
        }
        if (request.getGroupMentoring() && request.getMaxAttendees() == null) {
            throw new IllegalArgumentException("소그룹 멘토링 최대 인원을 선택해 주세요.");
        }

        long distinctStartTimeCount = request.getStartTimes().stream().distinct().count();
        if (distinctStartTimeCount != request.getStartTimes().size()) {
            throw new IllegalArgumentException("중복된 시간 슬롯은 등록할 수 없습니다.");
        }

        for (LocalTime startTime : request.getStartTimes()) {
            validateStartTime(startTime, mentor.getDurationMinutes());
        }
    }

    private void validateStartTime(LocalTime startTime, int durationMinutes) {
        if (startTime.isBefore(SLOT_START_MIN_TIME)) {
            throw new IllegalArgumentException("시간 슬롯은 오전 9시 이후부터 등록할 수 있습니다.");
        }
        if (startTime.getMinute() % 10 != 0) {
            throw new IllegalArgumentException("시간 슬롯은 10분 단위로만 등록할 수 있습니다.");
        }

        int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
        if (startMinutes + durationMinutes > 24 * 60) {
            throw new IllegalArgumentException("자정을 넘는 시간 슬롯은 등록할 수 없습니다.");
        }
    }

    private void validateLockedSlots(
            List<MentoringSlot> existingSlots,
            Map<Long, Integer> applicantCountMap,
            Set<LocalTime> requestedStartTimes,
            SlotType slotType,
            int maxAttendees
    ) {
        boolean hasLockedSlot = existingSlots.stream()
                .anyMatch(slot -> applicantCountMap.getOrDefault(slot.getSlotId(), 0) > 0);

        if (!hasLockedSlot) {
            return;
        }

        for (MentoringSlot slot : existingSlots) {
            if (applicantCountMap.getOrDefault(slot.getSlotId(), 0) == 0) {
                continue;
            }
            if (!requestedStartTimes.contains(slot.getStartTime())) {
                throw new IllegalArgumentException("신청된 시간 슬롯은 삭제할 수 없습니다.");
            }
            if (slot.getSlotType() != slotType
                    || (slotType == SlotType.ONE_TO_N && slot.getMaxAttendees() != maxAttendees)) {
                throw new IllegalArgumentException("해당 날짜에 이미 신청한 멘티가 있어 변경할 수 없습니다.");
            }
        }
    }

    private Map<Long, Integer> getApplicantCountMap(List<MentoringSlot> slots) {
        if (slots.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> slotIds = slots.stream()
                .map(MentoringSlot::getSlotId)
                .collect(Collectors.toList());

        Map<Long, Integer> applicantCountMap = new HashMap<>();
        mentoringApplicationRepository.countBySlotIdsAndStatusIn(slotIds, OCCUPYING_STATUSES)
                .forEach(row -> applicantCountMap.put((Long) row[0], ((Long) row[1]).intValue()));

        return applicantCountMap;
    }

    private LocalTime calculateEndTime(LocalTime startTime, int durationMinutes) {
        return startTime.plusMinutes(durationMinutes);
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
