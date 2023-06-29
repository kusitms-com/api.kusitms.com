package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.entity.TMPCorporateProject;
import com.kusitms.website.domain.admin.entity.TMPMeetupProject;
import com.kusitms.website.domain.admin.entity.TMPReview;
import com.kusitms.website.domain.admin.repository.TMPCorporateRepository;
import com.kusitms.website.domain.admin.repository.TMPMeetupRepository;
import com.kusitms.website.domain.admin.repository.TMPMeetupTeamRepository;
import com.kusitms.website.domain.admin.repository.TMPReviewRepository;
import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.domain.project.entity.CorporateProject;
import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.review.dto.response.ReviewDetailResponse;
import com.kusitms.website.domain.review.dto.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final S3Service s3Service;
    private final TMPCorporateRepository corporateRepository;
    private final TMPMeetupRepository meetupRepository;
    private final TMPMeetupTeamRepository meetupTeamRepository;
    private final TMPReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public ReviewResponse getReviews() {
        List<TMPReview> findReviews = reviewRepository.findAll();

        List<ReviewDetailResponse> reviewDetailResponses = findReviews.stream()
                .map(r -> new ReviewDetailResponse(r.getReviewId(), r.getName(), r.getTeam(), r.getReview()))
                .collect(Collectors.toList());

        return ReviewResponse.builder()
                .reviewCount(findReviews.size())
                .reviewList(reviewDetailResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public MeetupResponse getMeetupProjects() {
        List<TMPMeetupProject> findProjects = meetupRepository.findAllByOrderByCardinalDesc();

        List<MeetupDetailResponse> meetupDetailResponses = findProjects.stream()
                .map(p -> new MeetupDetailResponse(p, false))
                .collect(Collectors.toList());

        return MeetupResponse.builder()
                .meetupCount(meetupDetailResponses.size())
                .meetupList(meetupDetailResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public MeetupDetailResponse getMeetupProject(Long meetupId) {
        TMPMeetupProject findProject = meetupRepository.findById(meetupId).orElseThrow();

        return new MeetupDetailResponse(findProject, true);
    }

    @Transactional(readOnly = true)
    public CorporateResponse getCorporateProjects() {
        List<TMPCorporateProject> findProjects = corporateRepository.findAllByOrderByCardinalDesc();

        List<CorporateDetailResponse> detailResponses = findProjects.stream()
                .map(p -> new CorporateDetailResponse(p))
                .collect(Collectors.toList());

        return CorporateResponse.builder()
                .corporateCount(findProjects.size())
                .corporateList(detailResponses)
                .build();
    }
}
