package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.introduction.dto.request.ActivityRequest;
import com.kusitms.website.domain.introduction.dto.request.ExpertLectureRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.introduction.dto.request.ManagementTeamRequest;
import com.kusitms.website.domain.introduction.dto.request.OBLectureRequest;
import com.kusitms.website.domain.introduction.dto.response.*;
import com.kusitms.website.domain.introduction.entity.*;
import com.kusitms.website.domain.introduction.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class IntroService {

    private final S3Service s3Service;

    private final IntroRepository introRepository;
    private final ManageTeamRepository manageTeamRepository;
    private final ExpertLectureRepository expertLectureRepository;
    private final OBLectureRepository obLectureRepository;
    private final PartnerLogoRepository partnerLogoRepository;
    private final IntroMeetupImageRepository introMeetupImageRepository;
    private final ActivityRepository activityRepository;
    private final SponsorRepository sponsorRepository;

    private final String dirName = "intro";

    @Transactional(readOnly = true)
    public IntroResponse getIntroduction() {
        Introduction intro = introRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("학회 소개 정보가 존재하지 않습니다."));

        List<ManagementTeamResponse> managementTeamResponses = intro.getManageTeam().stream()
                .map(ManagementTeamResponse::fromEntity)
                .collect(Collectors.toList());

        List<ExpertLectureResponse> expertLectureResponses = intro.getExpertLecture().stream()
                .map(ExpertLectureResponse::fromEntity)
                .collect(Collectors.toList());

        List<OBLectureResponse> obLectureResponses = intro.getObLecture().stream()
                .map(OBLectureResponse::fromEntity)
                .collect(Collectors.toList());

        List<String> partnerLogoUrls = intro.getPartnerLogos().stream()
                .map(PartnerLogo::getImageUrl)
                .collect(Collectors.toList());

        List<String> meetupImageUrls = intro.getMeetupImages().stream()
                .map(IntroMeetupImage::getImageUrl)
                .collect(Collectors.toList());

        List<ActivityResponse> activityResponses = intro.getActivities().stream()
                .map(ActivityResponse::fromEntity)
                .collect(Collectors.toList());

        List<String> sponsorImageUrls = intro.getSponsors().stream()
                .map(Sponsor::getImageUrl)
                .collect(Collectors.toList());

        return IntroResponse.fromEntity(intro, partnerLogoUrls, meetupImageUrls,
                managementTeamResponses, expertLectureResponses, obLectureResponses,
                activityResponses, sponsorImageUrls);
    }

    @Transactional(readOnly = true)
    public AdminIntroResponse getAdminIntroduction() {
        Introduction intro = introRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("학회 소개 정보가 존재하지 않습니다."));

        List<ManagementTeamResponse> teams = intro.getManageTeam().stream()
                .map(ManagementTeamResponse::fromEntity)
                .collect(Collectors.toList());

        List<ExpertLectureResponse> expertLectures = intro.getExpertLecture().stream()
                .map(ExpertLectureResponse::fromEntity)
                .collect(Collectors.toList());

        List<OBLectureResponse> obLectures = intro.getObLecture().stream()
                .map(OBLectureResponse::fromEntity)
                .collect(Collectors.toList());

        List<String> partnerLogoUrls = intro.getPartnerLogos().stream()
                .map(PartnerLogo::getImageUrl)
                .collect(Collectors.toList());

        List<String> meetupImageUrls = intro.getMeetupImages().stream()
                .map(IntroMeetupImage::getImageUrl)
                .collect(Collectors.toList());

        List<ActivityResponse> activityResponses = intro.getActivities().stream()
                .map(ActivityResponse::fromEntity)
                .collect(Collectors.toList());

        List<String> sponsorImageUrls = intro.getSponsors().stream()
                .map(Sponsor::getImageUrl)
                .collect(Collectors.toList());

        return AdminIntroResponse.fromEntity(intro, partnerLogoUrls, meetupImageUrls,
                activityResponses, teams, expertLectures, obLectures, sponsorImageUrls);
    }

    @Transactional
    public void save(IntroRequest request) {
        String bannerImageUrl = uploadIfPresent(request.getBannerImageFile());
        String planningImageUrl = uploadIfPresent(request.getPlanningImage());
        String designImageUrl = uploadIfPresent(request.getDesignImage());
        String frontendImageUrl = uploadIfPresent(request.getFrontendImage());
        String backendImageUrl = uploadIfPresent(request.getBackendImage());

        Introduction introduction = IntroRequest.from(request, null,
                bannerImageUrl, planningImageUrl, designImageUrl, frontendImageUrl, backendImageUrl);
        introRepository.save(introduction);

        saveManageTeams(request, introduction);
        saveExpertLectures(request, introduction);
        saveOBLectures(request, introduction);
        savePartnerLogos(request.getPartnerLogoFiles(), introduction);
        saveMeetupImages(request.getMeetupImages(), introduction);
        saveActivities(request.getActivities(), introduction);
        saveSponsors(request.getSponsors(), introduction);
    }

    @Transactional
    public void updateIntroduction(IntroRequest request) {
        Introduction introduction = introRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("학회 소개 정보가 존재하지 않습니다."));

        String bannerImageUrl = uploadOrKeep(request.getBannerImageFile(), introduction.getBannerImageUrl());
        String planningImageUrl = uploadOrKeep(request.getPlanningImage(), introduction.getPlanningImageUrl());
        String designImageUrl = uploadOrKeep(request.getDesignImage(), introduction.getDesignImageUrl());
        String frontendImageUrl = uploadOrKeep(request.getFrontendImage(), introduction.getFrontendImageUrl());
        String backendImageUrl = uploadOrKeep(request.getBackendImage(), introduction.getBackendImageUrl());

        introduction.update(request.getBannerCardinal(),
                request.getBannerStatus(),
                request.getSlogan(),
                bannerImageUrl,
                request.getMemberCount(),
                request.getProjectCount(),
                request.getUniversityCount(),
                null,
                request.getIntroYoutubeLink(),
                planningImageUrl,
                designImageUrl,
                frontendImageUrl,
                backendImageUrl);

        introRepository.save(introduction);

        manageTeamRepository.deleteByIntroduction(introduction);
        expertLectureRepository.deleteByIntroduction(introduction);
        obLectureRepository.deleteByIntroduction(introduction);
        partnerLogoRepository.deleteByIntroduction(introduction);
        introMeetupImageRepository.deleteByIntroduction(introduction);
        activityRepository.deleteByIntroduction(introduction);
        sponsorRepository.deleteByIntroduction(introduction);

        saveManageTeams(request, introduction);
        saveExpertLectures(request, introduction);
        saveOBLectures(request, introduction);
        savePartnerLogos(request.getPartnerLogoFiles(), introduction);
        saveMeetupImages(request.getMeetupImages(), introduction);
        saveActivities(request.getActivities(), introduction);
        saveSponsors(request.getSponsors(), introduction);
    }

    private void saveManageTeams(IntroRequest request, Introduction introduction) {
        if (request.getTeams() == null) return;
        List<ManageTeam> manageTeams = request.getTeams().stream()
                .map(team -> {
                    String imageUrl = uploadIfPresent(team.getImageFile());
                    return ManagementTeamRequest.from(team, imageUrl, introduction);
                })
                .collect(Collectors.toList());
        manageTeamRepository.saveAll(manageTeams);
    }

    private void saveExpertLectures(IntroRequest request, Introduction introduction) {
        if (request.getExpertLecture() == null) return;
        List<ExpertLecture> expertLectures = request.getExpertLecture().stream()
                .map(lecture -> {
                    String imageUrl = uploadIfPresent(lecture.getImageFile());
                    return ExpertLectureRequest.from(lecture, imageUrl, introduction);
                })
                .collect(Collectors.toList());
        expertLectureRepository.saveAll(expertLectures);
    }

    private void saveOBLectures(IntroRequest request, Introduction introduction) {
        if (request.getObLecture() == null) return;
        List<OBLecture> obLectures = request.getObLecture().stream()
                .map(lecture -> {
                    String imageUrl = uploadIfPresent(lecture.getImageFile());
                    return OBLectureRequest.from(lecture, imageUrl, introduction);
                })
                .collect(Collectors.toList());
        obLectureRepository.saveAll(obLectures);
    }

    private void savePartnerLogos(List<MultipartFile> files, Introduction introduction) {
        if (files == null) return;
        List<PartnerLogo> logos = files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> PartnerLogo.builder()
                        .introduction(introduction)
                        .imageUrl(s3Service.uploadFile(f, dirName))
                        .build())
                .collect(Collectors.toList());
        partnerLogoRepository.saveAll(logos);
    }

    private void saveMeetupImages(List<MultipartFile> files, Introduction introduction) {
        if (files == null) return;
        List<IntroMeetupImage> images = files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> IntroMeetupImage.builder()
                        .introduction(introduction)
                        .imageUrl(s3Service.uploadFile(f, dirName))
                        .build())
                .collect(Collectors.toList());
        introMeetupImageRepository.saveAll(images);
    }

    private void saveActivities(List<ActivityRequest> activityRequests, Introduction introduction) {
        if (activityRequests == null) return;
        List<Activity> activities = activityRequests.stream()
                .map(req -> {
                    String imageUrl1 = uploadIfPresent(req.getImageFile1());
                    String imageUrl2 = uploadIfPresent(req.getImageFile2());
                    return ActivityRequest.from(req, imageUrl1, imageUrl2, introduction);
                })
                .collect(Collectors.toList());
        activityRepository.saveAll(activities);
    }

    private void saveSponsors(List<MultipartFile> files, Introduction introduction) {
        if (files == null) return;
        List<Sponsor> sponsors = files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> Sponsor.builder()
                        .introduction(introduction)
                        .imageUrl(s3Service.uploadFile(f, dirName))
                        .build())
                .collect(Collectors.toList());
        sponsorRepository.saveAll(sponsors);
    }

    private String uploadIfPresent(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return s3Service.uploadFile(file, dirName);
    }

    private String uploadOrKeep(MultipartFile file, String existingUrl) {
        if (file == null || file.isEmpty()) return existingUrl;
        return s3Service.uploadFile(file, dirName);
    }
}