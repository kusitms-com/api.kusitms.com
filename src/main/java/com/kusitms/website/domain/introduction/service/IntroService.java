package com.kusitms.website.domain.introduction.service;

import com.kusitms.website.domain.file.service.S3Service;
import com.kusitms.website.domain.introduction.dto.request.ActivityRequest;
import com.kusitms.website.domain.introduction.dto.request.ExpertLectureRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroImageRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroListItemRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.introduction.dto.request.ManagementTeamRequest;
import com.kusitms.website.domain.introduction.dto.request.OBLectureRequest;
import com.kusitms.website.domain.introduction.dto.response.*;
import com.kusitms.website.domain.introduction.entity.*;
import com.kusitms.website.domain.introduction.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
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

        List<IntroImageResponse> partnerLogos = intro.getPartnerLogos().stream()
                .map(logo -> IntroImageResponse.of(logo.getPartnerLogoId(), logo.getImageUrl()))
                .collect(Collectors.toList());

        List<IntroImageResponse> meetupImages = intro.getMeetupImages().stream()
                .map(image -> IntroImageResponse.of(image.getMeetupImageId(), image.getImageUrl()))
                .collect(Collectors.toList());

        List<ActivityResponse> activityResponses = intro.getActivities().stream()
                .map(ActivityResponse::fromEntity)
                .collect(Collectors.toList());

        List<IntroImageResponse> sponsors = intro.getSponsors().stream()
                .map(sponsor -> IntroImageResponse.of(sponsor.getSponsorId(), sponsor.getImageUrl()))
                .collect(Collectors.toList());

        return AdminIntroResponse.fromEntity(intro, partnerLogos, meetupImages,
                activityResponses, teams, expertLectures, obLectures, sponsors);
    }

    @Transactional
    public void saveOrUpdate(IntroRequest request) {
        Introduction introduction = introRepository.findFirstByOrderByIntroductionIdAsc().orElse(null);

        // 업로드를 시작하기 전에 검증해, 잘못된 요청으로 실패할 때 S3에 파일이 남지 않게 한다
        validateListItems(request, introduction);

        if (introduction == null) {
            introduction = saveIntroduction(request);
        } else {
            updateIntroduction(request, introduction);
        }
        syncLists(request, introduction);
    }

    private Introduction saveIntroduction(IntroRequest request) {
        String planningImageUrl = uploadIfPresent(request.getPlanningImage());
        String designImageUrl = uploadIfPresent(request.getDesignImage());
        String frontendImageUrl = uploadIfPresent(request.getFrontendImage());
        String backendImageUrl = uploadIfPresent(request.getBackendImage());

        Introduction introduction = IntroRequest.from(request, null,
                planningImageUrl, designImageUrl, frontendImageUrl, backendImageUrl);
        return introRepository.save(introduction);
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
    }

    private Introduction getIntroductionEntity() {
        return introRepository.findFirstByOrderByIntroductionIdAsc()
                .orElseThrow(() -> new EntityNotFoundException("학회 소개 정보가 존재하지 않습니다."));
    }

    // 목록은 생략하면 유지하고, 전달하면 id 기준으로 수정·추가·삭제하며 요청 순서대로 정렬한다
    private void syncLists(IntroRequest request, Introduction introduction) {
        sync(request.getTeams(), this::isTeamItem,
                introduction.getManageTeam(), ManageTeam::getTeamId, manageTeamRepository,
                (team, order) -> ManagementTeamRequest.from(team,
                        uploadIfPresent(team.getImageFile()), introduction, order),
                (entity, team, order) -> entity.update(team.getName(), team.getDescription(),
                        resolveImage(team.getImageFile(), team.getRemoveImage(), entity.getImageUrl()), order));

        sync(request.getExpertLecture(), this::isExpertLectureItem,
                introduction.getExpertLecture(), ExpertLecture::getLectureId, expertLectureRepository,
                (lecture, order) -> ExpertLectureRequest.from(lecture,
                        uploadIfPresent(lecture.getImageFile()), introduction, order),
                (entity, lecture, order) -> entity.update(lecture.getName(), lecture.getCorporation(), lecture.getDescription(),
                        resolveImage(lecture.getImageFile(), lecture.getRemoveImage(), entity.getImageUrl()), order));

        sync(request.getObLecture(), this::isObLectureItem,
                introduction.getObLecture(), OBLecture::getLectureId, obLectureRepository,
                (lecture, order) -> OBLectureRequest.from(lecture,
                        uploadIfPresent(lecture.getImageFile()), introduction, order),
                (entity, lecture, order) -> entity.update(lecture.getName(), lecture.getTopic(),
                        resolveImage(lecture.getImageFile(), lecture.getRemoveImage(), entity.getImageUrl()), order));

        sync(request.getActivities(), this::isActivityItem,
                introduction.getActivities(), Activity::getActivityId, activityRepository,
                (activity, order) -> ActivityRequest.from(activity,
                        uploadIfPresent(activity.getImageFile1()), uploadIfPresent(activity.getImageFile2()), introduction, order),
                (entity, activity, order) -> entity.update(activity.getName(), activity.getDescription(),
                        resolveImage(activity.getImageFile1(), activity.getRemoveImage1(), entity.getImageUrl1()),
                        resolveImage(activity.getImageFile2(), activity.getRemoveImage2(), entity.getImageUrl2()), order));

        sync(request.getPartnerLogos(), this::isImageItem,
                introduction.getPartnerLogos(), PartnerLogo::getPartnerLogoId, partnerLogoRepository,
                (image, order) -> PartnerLogo.builder().introduction(introduction)
                        .imageUrl(uploadIfPresent(image.getImageFile())).displayOrder(order).build(),
                (entity, image, order) -> entity.update(uploadOrKeep(image.getImageFile(), entity.getImageUrl()), order));

        sync(request.getMeetupImages(), this::isImageItem,
                introduction.getMeetupImages(), IntroMeetupImage::getMeetupImageId, introMeetupImageRepository,
                (image, order) -> IntroMeetupImage.builder().introduction(introduction)
                        .imageUrl(uploadIfPresent(image.getImageFile())).displayOrder(order).build(),
                (entity, image, order) -> entity.update(uploadOrKeep(image.getImageFile(), entity.getImageUrl()), order));

        sync(request.getSponsors(), this::isImageItem,
                introduction.getSponsors(), Sponsor::getSponsorId, sponsorRepository,
                (image, order) -> Sponsor.builder().introduction(introduction)
                        .imageUrl(uploadIfPresent(image.getImageFile())).displayOrder(order).build(),
                (entity, image, order) -> entity.update(uploadOrKeep(image.getImageFile(), entity.getImageUrl()), order));
    }

    // 부모(Introduction) 컬렉션을 기존 목록으로 사용한다
    private <E, R extends IntroListItemRequest> void sync(List<R> requests, Predicate<R> isItem,
                                                          List<E> parentCollection, Function<E, Long> idOf,
                                                          JpaRepository<E, Long> repository,
                                                          BiFunction<R, Integer, E> create, ItemUpdater<E, R> update) {
        if (requests == null) return;
        List<R> items = listItems(requests, isItem);
        List<E> existing = new ArrayList<>(parentCollection);

        Set<Long> keptIds = items.stream()
                .map(IntroListItemRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<E> removed = existing.stream()
                .filter(entity -> !keptIds.contains(idOf.apply(entity)))
                .collect(Collectors.toList());
        // 컬렉션에 남아 있으면 cascade로 다시 저장되므로 컬렉션에서도 뺀다
        parentCollection.removeAll(removed);
        repository.deleteAll(removed);

        Map<Long, E> existingById = existing.stream().collect(Collectors.toMap(idOf, Function.identity()));
        List<E> created = new ArrayList<>();
        for (int order = 0; order < items.size(); order++) {
            R item = items.get(order);
            if (item.getId() == null) {
                E entity = create.apply(item, order);
                created.add(entity);
                // 빌더가 컬렉션에 직접 추가하는 엔티티(운영진 등)와 그렇지 않은 엔티티를 맞춘다
                if (!parentCollection.contains(entity)) parentCollection.add(entity);
            } else {
                update.apply(existingById.get(item.getId()), item, order);
            }
        }
        repository.saveAll(created);
    }

    @FunctionalInterface
    private interface ItemUpdater<E, R> {
        void apply(E entity, R request, int order);
    }

    private void validateListItems(IntroRequest request, Introduction introduction) {
        validateIds(request.getTeams(), this::isTeamItem, introduction, Introduction::getManageTeam, ManageTeam::getTeamId, "운영진");
        validateIds(request.getExpertLecture(), this::isExpertLectureItem, introduction, Introduction::getExpertLecture, ExpertLecture::getLectureId, "전문가 강연");
        validateIds(request.getObLecture(), this::isObLectureItem, introduction, Introduction::getObLecture, OBLecture::getLectureId, "OB 강연");
        validateIds(request.getActivities(), this::isActivityItem, introduction, Introduction::getActivities, Activity::getActivityId, "활동");
        validateIds(request.getPartnerLogos(), this::isImageItem, introduction, Introduction::getPartnerLogos, PartnerLogo::getPartnerLogoId, "파트너사 로고");
        validateIds(request.getMeetupImages(), this::isImageItem, introduction, Introduction::getMeetupImages, IntroMeetupImage::getMeetupImageId, "밋업 사진");
        validateIds(request.getSponsors(), this::isImageItem, introduction, Introduction::getSponsors, Sponsor::getSponsorId, "후원사 로고");
    }

    // 요청 id는 이 소개의 같은 목록에 있는 항목만 허용하고, 한 요청에 같은 id가 두 번 나올 수 없다
    private <E, R extends IntroListItemRequest> void validateIds(List<R> requests, Predicate<R> isItem,
                                                                 Introduction introduction,
                                                                 Function<Introduction, List<E>> collectionOf,
                                                                 Function<E, Long> idOf, String listName) {
        if (requests == null) return;
        List<R> items = listItems(requests, isItem);
        if (items.stream().allMatch(item -> item.getId() == null)) return;

        Set<Long> existingIds = introduction == null ? Set.of()
                : collectionOf.apply(introduction).stream().map(idOf).collect(Collectors.toSet());
        Set<Long> seen = new HashSet<>();
        for (R item : items) {
            Long id = item.getId();
            if (id == null) continue;
            if (!existingIds.contains(id)) {
                throw new IllegalArgumentException(listName + " 항목 id가 올바르지 않습니다: " + id);
            }
            if (!seen.add(id)) {
                throw new IllegalArgumentException(listName + " 항목 id가 중복되었습니다: " + id);
            }
        }
    }

    // multipart 인덱스가 비면(teams[0], teams[2]) 중간 항목이 빈 객체로 채워지므로 실제 항목만 남긴다
    private static <R> List<R> listItems(List<R> requests, Predicate<R> isItem) {
        return requests.stream()
                .filter(Objects::nonNull)
                .filter(isItem)
                .collect(Collectors.toList());
    }

    private boolean isTeamItem(ManagementTeamRequest team) {
        return isNamedItem(team.getName(), team.getId(), team.getDescription(), team.getImageFile(),
                removeFlag(team.getRemoveImage()));
    }

    private boolean isExpertLectureItem(ExpertLectureRequest lecture) {
        return isNamedItem(lecture.getName(), lecture.getId(), lecture.getCorporation(), lecture.getDescription(),
                lecture.getImageFile(), removeFlag(lecture.getRemoveImage()));
    }

    private boolean isObLectureItem(OBLectureRequest lecture) {
        return isNamedItem(lecture.getName(), lecture.getId(), lecture.getTopic(), lecture.getImageFile(),
                removeFlag(lecture.getRemoveImage()));
    }

    private boolean isActivityItem(ActivityRequest activity) {
        return isNamedItem(activity.getName(), activity.getId(), activity.getDescription(),
                activity.getImageFile1(), activity.getImageFile2(),
                removeFlag(activity.getRemoveImage1()), removeFlag(activity.getRemoveImage2()));
    }

    // 삭제 요청(true)만 항목 내용으로 본다. 폼이 보내는 빈 값·false는 빈 항목 판단에 영향을 주지 않는다
    private static String removeFlag(Boolean remove) {
        return Boolean.TRUE.equals(remove) ? "true" : null;
    }

    // 이미지 목록은 기존 항목(id)이거나 새 파일이 있는 항목만 저장한다
    private boolean isImageItem(IntroImageRequest image) {
        return image.getId() != null || hasFile(image.getImageFile());
    }

    // 빈 항목은 건너뛰고, 내용은 있는데 이름이 없으면 기존 목록이 바뀌기 전에 400으로 거절한다
    private boolean isNamedItem(String name, Object... contents) {
        if (StringUtils.hasText(name)) return true;
        boolean hasContent = Stream.of(contents).anyMatch(content -> content instanceof MultipartFile
                ? hasFile((MultipartFile) content)
                : content != null && StringUtils.hasText(content.toString()));
        if (hasContent) {
            throw new IllegalArgumentException("이름이 없는 목록 항목이 있습니다.");
        }
        return false;
    }

    private static boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private String uploadIfPresent(MultipartFile file) {
        return resolveImage(file, false, null);
    }

    private String uploadOrKeep(MultipartFile file, String existingUrl) {
        return resolveImage(file, false, existingUrl);
    }

    // 새 파일 → 삭제 요청 → 기존 이미지 순으로 적용한다. 업로드는 여기서만 한다
    private String resolveImage(MultipartFile file, Boolean remove, String existingUrl) {
        if (hasFile(file)) return s3Service.uploadFile(file, dirName);
        return Boolean.TRUE.equals(remove) ? null : existingUrl;
    }
}
