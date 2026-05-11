package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.entity.TMPCorporateProject;
import com.kusitms.website.domain.admin.entity.TMPMeetupProject;
import com.kusitms.website.domain.admin.entity.TMPMeetupTeam;
import com.kusitms.website.domain.admin.entity.TMPReview;
import com.kusitms.website.domain.admin.repository.TMPCorporateRepository;
import com.kusitms.website.domain.admin.repository.TMPMeetupRepository;
import com.kusitms.website.domain.admin.repository.TMPMeetupTeamRepository;
import com.kusitms.website.domain.admin.repository.TMPReviewRepository;
import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.project.dto.request.CorporateRequest;
import com.kusitms.website.domain.project.dto.request.MeetupRequest;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.domain.project.entity.Team;
import com.kusitms.website.domain.review.dto.request.ReviewRequest;
import com.kusitms.website.domain.review.dto.response.ReviewDetailResponse;
import com.kusitms.website.domain.review.dto.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                .map(r -> new ReviewDetailResponse(r.getReviewId(), r.getName(), r.getCardinal(), r.getTeam(), r.getReview()))
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

    @Transactional
    public void saveMeetup(MeetupRequest request) {
        String logoUrl = s3Service.uploadFile(request.getLogoFile(), "meetup");
        String posterUrl = s3Service.uploadFile(request.getPosterFile(), "meetup");

        TMPMeetupProject meetup = MeetupRequest.from(request, logoUrl, posterUrl);

        meetupRepository.save(meetup);

        for(String name : request.getPlanner()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                            .meetup(meetup)
                            .team(Team.PLANNER)
                            .name(name)
                            .build());
        }
        for(String name : request.getDesigner()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                            .meetup(meetup)
                            .team(Team.DESIGNER)
                            .name(name)
                            .build());
        }
        for(String name : request.getFrontend()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                            .meetup(meetup)
                            .team(Team.FRONTEND)
                            .name(name)
                            .build());
        }
        for(String name : request.getBackend()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                            .meetup(meetup)
                            .team(Team.BACKEND)
                            .name(name)
                            .build());
        }
        for(String name : request.getIos()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.IOS)
                    .name(name)
                    .build());
        }
        for(String name : request.getAos()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.ANDROID)
                    .name(name)
                    .build());
        }
    }

    @Transactional
    public void updateMeetup(MeetupRequest request) {
        TMPMeetupProject meetup = meetupRepository.findById(request.getMeetupId()).orElseThrow();

        String logoUrl = s3Service.uploadFile(request.getLogoFile(), "meetup");
        String posterUrl = s3Service.uploadFile(request.getPosterFile(), "meetup");

        meetup.update(request.getCardinal(),
                request.getName(),
                request.getIntro(),
                request.getType(),
                request.getOneLineIntro(),
                logoUrl,
                posterUrl,
                request.getInstagramUrl(),
                request.getGithubUrl(),
                request.getAppUrl(),
                LocalDate.parse(request.getStartDate(), DateTimeFormatter.ISO_DATE),
                LocalDate.parse(request.getEndDate(), DateTimeFormatter.ISO_DATE),
                request.getTeamName());

        meetupTeamRepository.deleteByMeetupProject(meetup);

        for(String name : request.getPlanner()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.PLANNER)
                    .name(name)
                    .build());
        }
        for(String name : request.getDesigner()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.DESIGNER)
                    .name(name)
                    .build());
        }
        for(String name : request.getFrontend()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.FRONTEND)
                    .name(name)
                    .build());
        }
        for(String name : request.getBackend()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.BACKEND)
                    .name(name)
                    .build());
        }
        for(String name : request.getIos()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.IOS)
                    .name(name)
                    .build());
        }
        for(String name : request.getAos()) {
            meetupTeamRepository.save(TMPMeetupTeam.builder()
                    .meetup(meetup)
                    .team(Team.ANDROID)
                    .name(name)
                    .build());
        }

        meetupRepository.save(meetup);
    }

    @Transactional
    public void deleteMeetup(Long meetupId) {
        meetupRepository.deleteById(meetupId);
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

    @Transactional
    public void saveCorporate(CorporateRequest request) {
        String logoUrl = s3Service.uploadFile(request.getLogoFile(), "corporate");
        String bannerUrl = s3Service.uploadFile(request.getBannerFile(), "corporate");

        String category = "";
        for(int i = 0; i < request.getTag().size(); i++) {
            if(i == request.getTag().size() - 1) {
                category += request.getTag().get(i);
            } else {
                category +=  request.getTag().get(i) + "#";
            }
        }

        TMPCorporateProject corporate = CorporateRequest.from(request, category, logoUrl, bannerUrl);

        corporateRepository.save(corporate);
    }

    @Transactional
    public void updateCorporate(CorporateRequest request) {
        TMPCorporateProject corporate = corporateRepository.findById(request.getCorporateId()).orElseThrow();

        String logoUrl = s3Service.uploadFile(request.getLogoFile(), "corporate");
        String bannerUrl = s3Service.uploadFile(request.getBannerFile(), "corporate");

        String category = "";
        for(int i = 0; i < request.getTag().size(); i++) {
            if(i == request.getTag().size() - 1) {
                category += request.getTag().get(i);
            } else {
                category +=  request.getTag().get(i) + "#";
            }
        }

        corporate.update(request.getCardinal(),
                request.getName(),
                request.getContent(),
                logoUrl,
                bannerUrl,
                category);
    }

    @Transactional
    public void deleteCorporate(Long corporateId) {
        corporateRepository.deleteById(corporateId);
    }

    @Transactional
    public void saveReview(ReviewRequest request) {
        TMPReview review = ReviewRequest.from(request);
        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(ReviewRequest request) {
      TMPReview review = reviewRepository.findById(request.getReviewId()).orElseThrow();
      review.update(request.getName(),
              request.getTeam(),
              request.getReview());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
