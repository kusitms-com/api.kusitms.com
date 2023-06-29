package com.kusitms.website.domain.project.service;

import com.kusitms.website.domain.project.entity.MeetupProject;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.MeetupRepository;
import com.kusitms.website.domain.project.MeetupTeamRepository;
import com.kusitms.website.domain.file.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetupService {
    private final S3Service s3Service;
    private final MeetupRepository meetupRepository;
    private final MeetupTeamRepository meetupTeamRepository;

    @Transactional(readOnly = true)
    public MeetupResponse getMeetupProjects() {
        List<MeetupProject> findProjects = meetupRepository.findAllByOrderByCardinalDesc();

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
        MeetupProject findProject = meetupRepository.findById(meetupId).orElseThrow();

        return new MeetupDetailResponse(findProject, true);
    }

}
