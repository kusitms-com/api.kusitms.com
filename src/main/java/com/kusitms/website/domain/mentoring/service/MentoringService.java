package com.kusitms.website.domain.mentoring.service;

import com.kusitms.website.domain.mentoring.dto.request.MentoringApplyRequest;
import com.kusitms.website.domain.mentoring.dto.response.*;
import com.kusitms.website.domain.mentoring.entity.*;
import com.kusitms.website.domain.mentoring.repository.*;
import com.kusitms.website.domain.user.Member;
import com.kusitms.website.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoringService {

    private final MentorRepository mentorRepository;
    private final MentoringSlotRepository slotRepository;
    private final MentoringApplicationRepository applicationRepository;
    private final MentoringReviewRepository reviewRepository;
    private final MentoringReviewKeywordRepository reviewKeywordRepository;
    private final MemberRepository memberRepository;

    private static final List<ApplicationStatus> OCCUPYING_STATUSES =
            Arrays.asList(ApplicationStatus.PENDING, ApplicationStatus.ACTIVE);

    public MentorMainResponse getMainMentors() {
        List<Mentor> mentors = mentorRepository.findRandomActiveMentors(PageRequest.of(0, 4));
        List<MentorCardResponse> cards = buildMentorCards(mentors);
        return new MentorMainResponse(cards);
    }

    public MentorListResponse getMentorList(MentoringCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Mentor> mentorPage;

        if (category == null) {
            mentorPage = mentorRepository.findByActiveTrueOrderByCreatedAtDesc(pageable);
        } else {
            mentorPage = mentorRepository.findByActiveTrueAndCategoryOrderByCreatedAtDesc(category, pageable);
        }

        List<MentorCardResponse> cards = buildMentorCards(mentorPage.getContent());

        return MentorListResponse.builder()
                .totalCount(mentorPage.getTotalElements())
                .totalPages(mentorPage.getTotalPages())
                .currentPage(mentorPage.getNumber())
                .mentors(cards)
                .build();
    }

    public MentorDetailResponse getMentorDetail(Long mentorId, Long currentUserId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토입니다."));

        List<MentoringSlot> slots = slotRepository
                .findByMentorMentorIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
                        mentorId, LocalDate.now());

        List<MentoringSlotResponse> slotResponses = slots.stream()
                .map(slot -> {
                    int count = applicationRepository.countBySlotIdAndStatusIn(
                            slot.getSlotId(), OCCUPYING_STATUSES);
                    return MentoringSlotResponse.from(slot, count);
                })
                .collect(Collectors.toList());

        List<Object[]> keywordStats = reviewKeywordRepository.findKeywordStatsForMentor(mentorId);
        List<KeywordChipResponse> keywordChips = keywordStats.stream()
                .map(row -> new KeywordChipResponse(
                        (Long) row[0], (String) row[1], (Long) row[2]))
                .collect(Collectors.toList());

        Page<MentoringReview> reviewPage = reviewRepository
                .findByMentorMentorIdOrderByCreatedAtDesc(mentorId, PageRequest.of(0, 10));
        MentoringReviewListResponse reviewListResponse = buildReviewListResponse(reviewPage);

        boolean isOwnMentoring = mentor.getMember().getUserId().equals(currentUserId);
        boolean hasExistingApplication = applicationRepository
                .existsByMentorIdAndApplicantIdAndStatusIn(mentorId, currentUserId, OCCUPYING_STATUSES);

        return MentorDetailResponse.from(mentor, slotResponses, keywordChips,
                reviewListResponse, isOwnMentoring, hasExistingApplication);
    }

    public MentoringReviewListResponse getMentorReviews(Long mentorId, int page) {
        Page<MentoringReview> reviewPage = reviewRepository
                .findByMentorMentorIdOrderByCreatedAtDesc(mentorId, PageRequest.of(page, 10));
        return buildReviewListResponse(reviewPage);
    }

    @Transactional
    public void applyMentoring(Long mentorId, Long applicantUserId, MentoringApplyRequest request) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토입니다."));

        if (mentor.getMember().getUserId().equals(applicantUserId)) {
            throw new IllegalArgumentException("본인 멘토링에는 신청할 수 없습니다.");
        }

        if (applicationRepository.existsByMentorIdAndApplicantIdAndStatusIn(
                mentorId, applicantUserId, OCCUPYING_STATUSES)) {
            throw new IllegalArgumentException("이미 진행 중인 신청이 존재합니다.");
        }

        MentoringSlot slot = slotRepository.findByIdWithLock(request.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 슬롯입니다."));

        if (!slot.getMentor().getMentorId().equals(mentorId)) {
            throw new IllegalArgumentException("해당 멘토의 슬롯이 아닙니다.");
        }

        if (applicationRepository.existsBySlotSlotIdAndApplicantUserIdAndStatusIn(
                slot.getSlotId(), applicantUserId, OCCUPYING_STATUSES)) {
            throw new IllegalArgumentException("동일 슬롯에 중복 신청할 수 없습니다.");
        }

        int currentCount = applicationRepository.countBySlotIdAndStatusIn(
                slot.getSlotId(), OCCUPYING_STATUSES);

        if (slot.getSlotType() == SlotType.ONE_TO_ONE && currentCount >= 1) {
            throw new IllegalArgumentException("해당 시간대는 이미 예약되었습니다.");
        }
        if (slot.getSlotType() == SlotType.ONE_TO_N && currentCount >= slot.getMaxAttendees()) {
            throw new IllegalArgumentException("해당 시간대의 최대 인원에 도달했습니다.");
        }

        Member applicant = memberRepository.findById(applicantUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        MentoringApplication application = MentoringApplication.builder()
                .slot(slot)
                .applicant(applicant)
                .message(request.getMessage())
                .status(ApplicationStatus.PENDING)
                .build();

        applicationRepository.save(application);
    }

    private List<MentorCardResponse> buildMentorCards(List<Mentor> mentors) {
        if (mentors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> mentorIds = mentors.stream()
                .map(Mentor::getMentorId)
                .collect(Collectors.toList());

        List<Object[]> keywordRows = reviewKeywordRepository.findTopKeywordsForMentors(mentorIds);

        Map<Long, String> badgeMap = new HashMap<>();
        for (Object[] row : keywordRows) {
            Long mId = (Long) row[0];
            String keyword = (String) row[1];
            badgeMap.putIfAbsent(mId, keyword);
        }

        return mentors.stream()
                .map(m -> MentorCardResponse.from(m, badgeMap.get(m.getMentorId())))
                .collect(Collectors.toList());
    }

    private MentoringReviewListResponse buildReviewListResponse(Page<MentoringReview> page) {
        List<MentoringReviewDetailResponse> reviews = page.getContent().stream()
                .map(MentoringReviewDetailResponse::from)
                .collect(Collectors.toList());

        return MentoringReviewListResponse.builder()
                .totalCount(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .reviews(reviews)
                .build();
    }
}
