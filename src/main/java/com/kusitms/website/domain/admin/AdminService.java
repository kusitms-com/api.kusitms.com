package com.kusitms.website.domain.admin;

import com.kusitms.website.domain.admin.entity.TMPBlogReview;
import com.kusitms.website.domain.admin.repository.TMPBlogReviewRepository;
import com.kusitms.website.domain.blog.dto.response.BlogReviewDetailResponse;
import com.kusitms.website.domain.blog.dto.response.BlogReviewResponse;
import com.kusitms.website.domain.blog.dto.request.BlogReviewRequest;
import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.project.CorporateRepository;
import com.kusitms.website.domain.project.MeetupRepository;
import com.kusitms.website.domain.project.TagRepository;
import com.kusitms.website.domain.project.dto.request.CorporateRequest;
import com.kusitms.website.domain.project.dto.request.MeetupRequest;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.domain.project.entity.CorporateProject;
import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.project.entity.MeetupTeam;
import com.kusitms.website.domain.project.entity.Tag;
import com.kusitms.website.domain.project.entity.Team;
import com.kusitms.website.domain.review.ReviewRepository;
import com.kusitms.website.domain.review.dto.request.ReviewRequest;
import com.kusitms.website.domain.review.dto.response.ReviewDetailResponse;
import com.kusitms.website.domain.review.dto.response.ReviewResponse;
import com.kusitms.website.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final S3Service s3Service;
    private final CorporateRepository corporateRepository;
    private final MeetupRepository meetupRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final TMPBlogReviewRepository blogReviewRepository;

    @Transactional(readOnly = true)
    public BlogReviewResponse getBlogReviews() {
        List<TMPBlogReview> findBlogReviews = blogReviewRepository.findAllByOrderByBlogReviewIdDesc();

        List<BlogReviewDetailResponse> blogReviewDetails = findBlogReviews.stream()
                .map(BlogReviewDetailResponse::new)
                .collect(Collectors.toList());

        return BlogReviewResponse.builder()
                .blogReviewCount(blogReviewDetails.size())
                .blogReviewList(blogReviewDetails)
                .build();
    }

    @Transactional
    public void saveBlogReview(BlogReviewRequest request) {
        String thumbnailUrl = null;
        if (request.getThumbnailFile() != null && !request.getThumbnailFile().isEmpty()) {
            thumbnailUrl = s3Service.uploadFile(request.getThumbnailFile(), "blog-review");
        }

        TMPBlogReview blogReview = BlogReviewRequest.from(request, thumbnailUrl);
        blogReviewRepository.save(blogReview);
    }

    @Transactional
    public void updateBlogReview(BlogReviewRequest request) {
        TMPBlogReview blogReview = blogReviewRepository.findById(request.getBlogReviewId()).orElseThrow();

        String thumbnailUrl = blogReview.getThumbnailUrl();
        if (request.getThumbnailFile() != null && !request.getThumbnailFile().isEmpty()) {
            thumbnailUrl = s3Service.uploadFile(request.getThumbnailFile(), "blog-review");
        }

        blogReview.update(
                request.getCardinal(),
                request.getPart(),
                request.getActivity(),
                thumbnailUrl,
                request.getTitle(),
                request.getPreviewText()
        );
    }

    @Transactional
    public void deleteBlogReview(Long blogReviewId) {
        blogReviewRepository.deleteById(blogReviewId);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviews() {
        List<Review> findReviews = reviewRepository.findAllByOrderByReviewIdDesc();

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
        List<MeetupProject> findProjects = meetupRepository.findAllByOrderByCardinalDesc();

        List<MeetupDetailResponse> meetupDetailResponses = findProjects.stream()
                .map(p -> {
                    MeetupDetailResponse response = MeetupDetailResponse.forAdmin(p, false);
                    response.setTags(toTagNames(p.getTags()));
                    return response;
                })
                .collect(Collectors.toList());

        return MeetupResponse.builder()
                .meetupCount(meetupDetailResponses.size())
                .meetupList(meetupDetailResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public MeetupDetailResponse getMeetupProject(Long meetupId) {
        MeetupProject findProject = meetupRepository.findById(meetupId).orElseThrow();

        MeetupDetailResponse response = MeetupDetailResponse.forAdmin(findProject, true);
        response.setTags(toTagNames(findProject.getTags()));
        return response;
    }

    @Transactional
    public void saveMeetup(MeetupRequest request) {
        String logoUrl = s3Service.uploadFile(request.getLogoFile(), "meetup");
        String posterUrl = s3Service.uploadFile(request.getPosterFile(), "meetup");
        String webThumbnailUrl = uploadOrKeep(request.getWebThumbnailFile(), null, "meetup");

        MeetupProject meetup = MeetupRequest.from(request, logoUrl, posterUrl, webThumbnailUrl);
        addTeamMembers(meetup, request);
        meetup.updateTags(resolveTags(request.getTag()));

        meetupRepository.save(meetup);
    }

    @Transactional
    public void updateMeetup(MeetupRequest request) {
        MeetupProject meetup = meetupRepository.findById(request.getMeetupId()).orElseThrow();

        String logoUrl = uploadOrKeep(request.getLogoFile(), meetup.getLogoUrl(), "meetup");
        String posterUrl = uploadOrKeep(request.getPosterFile(), meetup.getPosterUrl(), "meetup");
        String webThumbnailUrl = uploadOrKeep(request.getWebThumbnailFile(), meetup.getWebThumbnailUrl(), "meetup");

        meetup.update(request.getCardinal(),
                request.getName(),
                request.getIntro(),
                request.getType(),
                request.getOneLineIntro(),
                logoUrl,
                posterUrl,
                webThumbnailUrl,
                request.getBehanceUrl(),
                request.getInstagramUrl(),
                request.getGithubUrl(),
                request.getAppUrl(),
                LocalDate.parse(request.getStartDate(), DateTimeFormatter.ISO_DATE),
                LocalDate.parse(request.getEndDate(), DateTimeFormatter.ISO_DATE),
                request.getTeamName());

        meetup.clearTeam();
        addTeamMembers(meetup, request);
        meetup.updateTags(resolveTags(request.getTag()));
    }

    @Transactional
    public void deleteMeetup(Long meetupId) {
        meetupRepository.deleteById(meetupId);
    }

    @Transactional(readOnly = true)
    public CorporateResponse getCorporateProjects() {
        List<CorporateProject> findProjects = corporateRepository.findAllByOrderByCardinalDesc();

        List<CorporateDetailResponse> detailResponses = findProjects.stream()
                .map(this::toCorporateDetailResponse)
                .collect(Collectors.toList());

        return CorporateResponse.builder()
                .corporateCount(findProjects.size())
                .corporateList(detailResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public CorporateDetailResponse getCorporateProject(Long corporateId) {
        CorporateProject findProject = corporateRepository.findById(corporateId).orElseThrow();
        return toCorporateDetailResponse(findProject);
    }

    @Transactional
    public void saveCorporate(CorporateRequest request) {
        String logoUrl = s3Service.uploadFile(request.getLogoFile(), "corporate");
        String bannerUrl = s3Service.uploadFile(request.getBannerFile(), "corporate");

        Set<Tag> tags = resolveTags(request.getTag());
        CorporateProject corporate = CorporateRequest.from(request, joinCategory(tags), logoUrl, bannerUrl);
        corporate.updateTags(tags);

        corporateRepository.save(corporate);
    }

    @Transactional
    public void updateCorporate(CorporateRequest request) {
        CorporateProject corporate = corporateRepository.findById(request.getCorporateId()).orElseThrow();

        String logoUrl = uploadOrKeep(request.getLogoFile(), corporate.getLogoUrl(), "corporate");
        String bannerUrl = uploadOrKeep(request.getBannerFile(), corporate.getBannerUrl(), "corporate");

        Set<Tag> tags = resolveTags(request.getTag());
        corporate.update(request.getCardinal(),
                request.getName(),
                request.getContent(),
                logoUrl,
                bannerUrl,
                joinCategory(tags));
        corporate.updateTags(tags);
    }

    @Transactional
    public void deleteCorporate(Long corporateId) {
        corporateRepository.deleteById(corporateId);
    }

    @Transactional
    public void saveReview(ReviewRequest request) {
        Review review = ReviewRequest.from(request);
        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(ReviewRequest request) {
        Review review = reviewRepository.findById(request.getReviewId()).orElseThrow();
        review.update(request.getName(),
                request.getCardinal(),
                request.getTeam(),
                request.getReview());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private void addTeamMembers(MeetupProject meetup, MeetupRequest request) {
        addTeamMembers(meetup, Team.PLANNER, request.getPlanner());
        addTeamMembers(meetup, Team.DESIGNER, request.getDesigner());
        addTeamMembers(meetup, Team.FRONTEND, request.getFrontend());
        addTeamMembers(meetup, Team.BACKEND, request.getBackend());
        addTeamMembers(meetup, Team.IOS, request.getIos());
        addTeamMembers(meetup, Team.ANDROID, request.getAos());
    }

    private void addTeamMembers(MeetupProject meetup, Team team, List<String> names) {
        if (names == null) return;
        for (String name : names) {
            // MeetupTeam 생성자가 meetup.getTeam()에 추가하므로 cascade로 함께 저장됨
            MeetupTeam.builder()
                    .meetup(meetup)
                    .team(team)
                    .name(name)
                    .build();
        }
    }

    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new LinkedHashSet<>();
        if (tagNames == null) return tags;

        for (String tagName : tagNames) {
            String name = normalizeTagName(tagName);
            if (name.isEmpty()) continue;
            tags.add(tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name))));
        }
        return tags;
    }

    private String normalizeTagName(String tagName) {
        if (tagName == null) return "";
        String name = tagName.trim();
        while (name.startsWith("#")) {
            name = name.substring(1).trim();
        }
        return name;
    }

    private String joinCategory(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining("#"));
    }

    private List<String> toTagNames(Set<Tag> tags) {
        return tags.stream()
                .map(tag -> "#" + tag.getName())
                .collect(Collectors.toList());
    }

    private CorporateDetailResponse toCorporateDetailResponse(CorporateProject project) {
        CorporateDetailResponse response = new CorporateDetailResponse(project);
        response.setTags(toTagNames(project.getTags()));
        return response;
    }

    private String uploadOrKeep(MultipartFile file, String existingUrl, String dirName) {
        if (file == null || file.isEmpty()) return existingUrl;
        return s3Service.uploadFile(file, dirName);
    }
}
