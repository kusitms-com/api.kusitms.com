package com.kusitms.website.domain.introduction.service;

import com.kusitms.website.domain.file.service.S3Service;
import com.kusitms.website.domain.introduction.dto.request.ActivityRequest;
import com.kusitms.website.domain.introduction.dto.request.ExpertLectureRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroImageRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.introduction.dto.request.ManagementTeamRequest;
import com.kusitms.website.domain.introduction.dto.request.OBLectureRequest;
import com.kusitms.website.domain.introduction.dto.response.*;
import com.kusitms.website.domain.introduction.entity.*;
import com.kusitms.website.domain.introduction.repository.*;
import com.kusitms.website.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        Introduction intro = getIntroductionEntity();

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
        Introduction intro = getIntroductionEntity();

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
    public void saveOrUpdate(IntroRequest request) {
        // 업로드를 시작하기 전에 검증해, 잘못된 URL로 실패할 때 S3에 파일이 남지 않게 한다
        validateExistingImageUrls(request);

        Optional<Introduction> introduction = introRepository.findFirstByOrderByIntroductionIdAsc();

        if (introduction.isPresent()) {
            updateIntroduction(request, introduction.get());
            return;
        }

        saveIntroduction(request);
    }

    private void saveIntroduction(IntroRequest request) {
        String planningImageUrl = uploadIfPresent(request.getPlanningImage());
        String designImageUrl = uploadIfPresent(request.getDesignImage());
        String frontendImageUrl = uploadIfPresent(request.getFrontendImage());
        String backendImageUrl = uploadIfPresent(request.getBackendImage());

        Introduction introduction = IntroRequest.from(request, null,
                planningImageUrl, designImageUrl, frontendImageUrl, backendImageUrl);
        introRepository.save(introduction);

        saveManageTeams(request, introduction);
        saveExpertLectures(request, introduction);
        saveOBLectures(request, introduction);
        savePartnerLogos(request.getPartnerLogos(), introduction);
        saveMeetupImages(request.getMeetupImages(), introduction);
        saveActivities(request.getActivities(), introduction);
        saveSponsors(request.getSponsors(), introduction);
    }

    private void updateIntroduction(IntroRequest request, Introduction introduction) {
        String planningImageUrl = uploadOrKeep(request.getPlanningImage(), introduction.getPlanningImageUrl());
        String designImageUrl = uploadOrKeep(request.getDesignImage(), introduction.getDesignImageUrl());
        String frontendImageUrl = uploadOrKeep(request.getFrontendImage(), introduction.getFrontendImageUrl());
        String backendImageUrl = uploadOrKeep(request.getBackendImage(), introduction.getBackendImageUrl());

        introduction.update(request.getBannerCardinal(),
                request.getBannerStatus(),
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

        replaceManageTeams(request, introduction);
        replaceExpertLectures(request, introduction);
        replaceOBLectures(request, introduction);
        replacePartnerLogos(request.getPartnerLogos(), introduction);
        replaceMeetupImages(request.getMeetupImages(), introduction);
        replaceActivities(request.getActivities(), introduction);
        replaceSponsors(request.getSponsors(), introduction);
    }

    private Introduction getIntroductionEntity() {
        return introRepository.findFirstByOrderByIntroductionIdAsc()
                .orElseThrow(() -> new EntityNotFoundException("학회 소개 정보가 존재하지 않습니다."));
    }

    private void replaceManageTeams(IntroRequest request, Introduction introduction) {
        if (request.getTeams() == null) return;
        manageTeamRepository.deleteByIntroduction(introduction);
        saveManageTeams(request, introduction);
    }

    private void replaceExpertLectures(IntroRequest request, Introduction introduction) {
        if (request.getExpertLecture() == null) return;
        expertLectureRepository.deleteByIntroduction(introduction);
        saveExpertLectures(request, introduction);
    }

    private void replaceOBLectures(IntroRequest request, Introduction introduction) {
        if (request.getObLecture() == null) return;
        obLectureRepository.deleteByIntroduction(introduction);
        saveOBLectures(request, introduction);
    }

    private void replacePartnerLogos(List<IntroImageRequest> images, Introduction introduction) {
        if (images == null) return;
        partnerLogoRepository.deleteByIntroduction(introduction);
        savePartnerLogos(images, introduction);
    }

    private void replaceMeetupImages(List<IntroImageRequest> images, Introduction introduction) {
        if (images == null) return;
        introMeetupImageRepository.deleteByIntroduction(introduction);
        saveMeetupImages(images, introduction);
    }

    private void replaceActivities(List<ActivityRequest> activities, Introduction introduction) {
        if (activities == null) return;
        activityRepository.deleteByIntroduction(introduction);
        saveActivities(activities, introduction);
    }

    private void replaceSponsors(List<IntroImageRequest> images, Introduction introduction) {
        if (images == null) return;
        sponsorRepository.deleteByIntroduction(introduction);
        saveSponsors(images, introduction);
    }

    private void saveManageTeams(IntroRequest request, Introduction introduction) {
        if (request.getTeams() == null) return;
        List<ManageTeam> manageTeams = request.getTeams().stream()
                .filter(item -> item != null && StringUtils.hasText(item.getName()))
                .map(team -> {
                    String imageUrl = uploadOrKeepUrl(team.getImageFile(), team.getImageUrl());
                    return ManagementTeamRequest.from(team, imageUrl, introduction);
                })
                .collect(Collectors.toList());
        manageTeamRepository.saveAll(manageTeams);
    }

    private void saveExpertLectures(IntroRequest request, Introduction introduction) {
        if (request.getExpertLecture() == null) return;
        List<ExpertLecture> expertLectures = request.getExpertLecture().stream()
                .filter(item -> item != null && StringUtils.hasText(item.getName()))
                .map(lecture -> {
                    String imageUrl = uploadOrKeepUrl(lecture.getImageFile(), lecture.getImageUrl());
                    return ExpertLectureRequest.from(lecture, imageUrl, introduction);
                })
                .collect(Collectors.toList());
        expertLectureRepository.saveAll(expertLectures);
    }

    private void saveOBLectures(IntroRequest request, Introduction introduction) {
        if (request.getObLecture() == null) return;
        List<OBLecture> obLectures = request.getObLecture().stream()
                .filter(item -> item != null && StringUtils.hasText(item.getName()))
                .map(lecture -> {
                    String imageUrl = uploadOrKeepUrl(lecture.getImageFile(), lecture.getImageUrl());
                    return OBLectureRequest.from(lecture, imageUrl, introduction);
                })
                .collect(Collectors.toList());
        obLectureRepository.saveAll(obLectures);
    }

    private void savePartnerLogos(List<IntroImageRequest> images, Introduction introduction) {
        List<PartnerLogo> logos = resolveImages(images).stream()
                .map(imageUrl -> PartnerLogo.builder()
                        .introduction(introduction)
                        .imageUrl(imageUrl)
                        .build())
                .collect(Collectors.toList());
        partnerLogoRepository.saveAll(logos);
    }

    private void saveMeetupImages(List<IntroImageRequest> images, Introduction introduction) {
        List<IntroMeetupImage> meetupImages = resolveImages(images).stream()
                .map(imageUrl -> IntroMeetupImage.builder()
                        .introduction(introduction)
                        .imageUrl(imageUrl)
                        .build())
                .collect(Collectors.toList());
        introMeetupImageRepository.saveAll(meetupImages);
    }

    private void saveActivities(List<ActivityRequest> activityRequests, Introduction introduction) {
        if (activityRequests == null) return;
        List<Activity> activities = activityRequests.stream()
                .filter(item -> item != null && StringUtils.hasText(item.getName()))
                .map(req -> {
                    String imageUrl1 = uploadOrKeepUrl(req.getImageFile1(), req.getImageUrl1());
                    String imageUrl2 = uploadOrKeepUrl(req.getImageFile2(), req.getImageUrl2());
                    return ActivityRequest.from(req, imageUrl1, imageUrl2, introduction);
                })
                .collect(Collectors.toList());
        activityRepository.saveAll(activities);
    }

    private void saveSponsors(List<IntroImageRequest> images, Introduction introduction) {
        List<Sponsor> sponsors = resolveImages(images).stream()
                .map(imageUrl -> Sponsor.builder()
                        .introduction(introduction)
                        .imageUrl(imageUrl)
                        .build())
                .collect(Collectors.toList());
        sponsorRepository.saveAll(sponsors);
    }

    // 이미지 목록은 요청 순서를 유지하고, 파일도 기존 URL도 없는 항목은 건너뛴다
    private List<String> resolveImages(List<IntroImageRequest> images) {
        if (images == null) return List.of();
        return images.stream()
                .filter(Objects::nonNull)
                .map(image -> uploadOrKeepUrl(image.getImageFile(), image.getImageUrl()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String uploadIfPresent(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return s3Service.uploadFile(file, dirName);
    }

    private String uploadOrKeep(MultipartFile file, String existingUrl) {
        if (file == null || file.isEmpty()) return existingUrl;
        return s3Service.uploadFile(file, dirName);
    }

    private void validateExistingImageUrls(IntroRequest request) {
        nonNullItems(request.getTeams()).stream()
                .filter(t -> isNamedItem(t.getName(), t.getDescription(), t.getImageFile(), t.getImageUrl()))
                .forEach(t -> validateKeptImage(t.getImageFile(), t.getImageUrl()));
        nonNullItems(request.getExpertLecture()).stream()
                .filter(l -> isNamedItem(l.getName(), l.getCorporation(), l.getDescription(), l.getImageFile(), l.getImageUrl()))
                .forEach(l -> validateKeptImage(l.getImageFile(), l.getImageUrl()));
        nonNullItems(request.getObLecture()).stream()
                .filter(l -> isNamedItem(l.getName(), l.getTopic(), l.getImageFile(), l.getImageUrl()))
                .forEach(l -> validateKeptImage(l.getImageFile(), l.getImageUrl()));
        nonNullItems(request.getActivities()).stream()
                .filter(a -> isNamedItem(a.getName(), a.getDescription(),
                        a.getImageFile1(), a.getImageFile2(), a.getImageUrl1(), a.getImageUrl2()))
                .forEach(a -> {
                    validateKeptImage(a.getImageFile1(), a.getImageUrl1());
                    validateKeptImage(a.getImageFile2(), a.getImageUrl2());
                });
        Stream.of(request.getPartnerLogos(), request.getMeetupImages(), request.getSponsors())
                .flatMap(images -> nonNullItems(images).stream())
                .forEach(i -> validateKeptImage(i.getImageFile(), i.getImageUrl()));
    }

    // 인덱스를 건너뛰어 생긴 빈 항목은 저장 시 건너뛰고, 내용은 있는데 이름이 없으면 기존 목록이 지워지기 전에 400으로 거절한다
    private boolean isNamedItem(String name, Object... contents) {
        if (StringUtils.hasText(name)) return true;
        boolean hasContent = Stream.of(contents).anyMatch(c ->
                c instanceof MultipartFile ? !((MultipartFile) c).isEmpty() : c != null && StringUtils.hasText(c.toString()));
        if (hasContent) {
            throw new IllegalArgumentException("이름이 없는 목록 항목이 있습니다.");
        }
        return false;
    }

    // 새 파일이 있으면 기존 URL은 쓰지 않으므로 검증하지 않는다
    private void validateKeptImage(MultipartFile file, String existingUrl) {
        if (file == null || file.isEmpty()) toIntroKey(existingUrl);
    }

    // multipart 인덱스가 비면(teams[0], teams[2]) 중간 항목이 빈 객체로 채워지므로, 목록 저장 시 이름 없는 항목은 건너뛴다
    private static <T> List<T> nonNullItems(List<T> list) {
        if (list == null) return List.of();
        return list.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    // 소개 이미지 폴더(intro/)의 키만 허용한다. 다른 도메인 이미지를 소개 페이지에 연결하지 못하게 한다
    private String toIntroKey(String url) {
        String key = S3Util.toKey(url);
        if (key != null && (!key.startsWith(dirName + "/") || hasDotSegment(key))) {
            throw new IllegalArgumentException("소개 이미지가 아닌 URL입니다: " + url);
        }
        return key;
    }

    // 브라우저가 경로의 . / .. 세그먼트(%2e 인코딩 포함)를 정규화하므로 intro/ 폴더 제한을 우회하지 못하게 막는다
    // 파일명 안의 ..(logo..v2.png)는 세그먼트가 아니므로 허용한다
    private static boolean hasDotSegment(String key) {
        return Stream.of(key.split("/"))
                .map(segment -> segment.replaceAll("(?i)%2e", "."))
                .anyMatch(segment -> segment.equals(".") || segment.equals(".."));
    }

    // 목록은 행을 지우고 다시 만들므로, 새 파일이 없으면 요청으로 받은 기존 이미지 URL을 키로 되돌려 유지한다
    private String uploadOrKeepUrl(MultipartFile file, String existingUrl) {
        if (file != null && !file.isEmpty()) return s3Service.uploadFile(file, dirName);
        return toIntroKey(existingUrl);
    }
}
