package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.introduction.dto.request.ExpertLectureRequest;
import com.kusitms.website.domain.introduction.dto.request.IntroRequest;
import com.kusitms.website.domain.introduction.dto.request.ManagementTeamRequest;
import com.kusitms.website.domain.introduction.dto.request.OBLectureRequest;
import com.kusitms.website.domain.introduction.dto.response.ExpertLectureResponse;
import com.kusitms.website.domain.introduction.dto.response.IntroResponse;
import com.kusitms.website.domain.introduction.dto.response.ManagementTeamResponse;
import com.kusitms.website.domain.introduction.dto.response.OBLectureResponse;
import com.kusitms.website.domain.introduction.entity.ExpertLecture;
import com.kusitms.website.domain.introduction.entity.Introduction;
import com.kusitms.website.domain.introduction.entity.ManageTeam;
import com.kusitms.website.domain.introduction.entity.OBLecture;
import com.kusitms.website.domain.introduction.repository.ExpertLectureRepository;
import com.kusitms.website.domain.introduction.repository.IntroRepository;
import com.kusitms.website.domain.introduction.repository.ManageTeamRepository;
import com.kusitms.website.domain.introduction.repository.OBLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntroService {

    private final S3Service s3Service;

    private final IntroRepository introRepository;
    private final ManageTeamRepository manageTeamRepository;
    private final ExpertLectureRepository expertLectureRepository;
    private final OBLectureRepository obLectureRepository;


    private final String dirName = "intro";

    @Transactional(readOnly = true)
    public IntroResponse getIntroduction() {
        // 학회 소개 페이지 정보는 단일
        Introduction intro = introRepository.findAll().get(0);

        List<ManagementTeamResponse> managementTeamResponses = intro.getManageTeam().stream()
                .map(ManagementTeamResponse :: fromEntity)
                .collect(Collectors.toList());

        List<ExpertLectureResponse> expertLectureResponses = intro.getExpertLecture().stream()
                .map(ExpertLectureResponse :: fromEntity)
                .collect(Collectors.toList());

        List<OBLectureResponse> obLectureResponses = intro.getObLecture().stream()
                .map(OBLectureResponse :: fromEntity)
                .collect(Collectors.toList());

        return IntroResponse.fromEntity(intro, managementTeamResponses, expertLectureResponses, obLectureResponses);
    }

    @Transactional()
    public void save(IntroRequest request) {
        String partnerImageUrl = s3Service.uploadFile(request.getPartnerLogoFile(), dirName);
        Introduction introduction = IntroRequest.from(request, partnerImageUrl);
        introRepository.save(introduction);

        List<ManageTeam> manageTeam = request.getTeams().stream()
                .map(team -> ManagementTeamRequest.from(team, s3Service.uploadFile(team.getImageFile(), dirName), introduction))
                .collect(Collectors.toList());

        List<ExpertLecture> expertLecture = request.getExpertLecture().stream()
                .map(lecture -> ExpertLectureRequest.from(lecture, s3Service.uploadFile(lecture.getImageFile(), dirName), introduction))
                .collect(Collectors.toList());

        List<OBLecture> obLecture = request.getObLecture().stream()
                .map(lecture -> OBLectureRequest.from(lecture, s3Service.uploadFile(lecture.getImageFile(), dirName), introduction))
                .collect(Collectors.toList());

        manageTeamRepository.saveAll(manageTeam);
        expertLectureRepository.saveAll(expertLecture);
        obLectureRepository.saveAll(obLecture);

        return;
    }

    @Transactional()
    public void updateIntroduction(IntroRequest request) {
        String partnerImageUrl = s3Service.uploadFile(request.getPartnerLogoFile(), dirName);
        Introduction introduction = introRepository.findAll().get(0);
        introduction.update(request.getBannerCardinal(),
                request.getBannerStatus(),
                request.getMemberCount(),
                request.getProjectCount(),
                request.getUniversityCount(),
                partnerImageUrl,
                request.getIntroYoutubeLink());

        introRepository.save(introduction);

        manageTeamRepository.deleteAll();
        expertLectureRepository.deleteAll();
        obLectureRepository.deleteAll();

        List<ManageTeam> manageTeam = request.getTeams().stream()
                .map(team -> ManagementTeamRequest.from(team, s3Service.uploadFile(team.getImageFile(), dirName), introduction))
                .collect(Collectors.toList());

        List<ExpertLecture> expertLecture = request.getExpertLecture().stream()
                .map(lecture -> ExpertLectureRequest.from(lecture, s3Service.uploadFile(lecture.getImageFile(), dirName), introduction))
                .collect(Collectors.toList());

        List<OBLecture> obLecture = request.getObLecture().stream()
                .map(lecture -> OBLectureRequest.from(lecture, s3Service.uploadFile(lecture.getImageFile(), dirName), introduction))
                .collect(Collectors.toList());

        manageTeamRepository.saveAll(manageTeam);
        expertLectureRepository.saveAll(expertLecture);
        obLectureRepository.saveAll(obLecture);

        return;
    }

}
