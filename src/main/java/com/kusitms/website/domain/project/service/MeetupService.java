package com.kusitms.website.domain.project.service;

import com.kusitms.website.domain.project.MeetupRepository;
import com.kusitms.website.domain.project.dto.response.MeetupDetailResponse;
import com.kusitms.website.domain.project.dto.response.MeetupResponse;
import com.kusitms.website.domain.project.entity.MeetupProject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetupService {

    private final MeetupRepository meetupRepository;

    public MeetupResponse getMeetupProjects(String order, Integer cardinal, String batch) {
        List<MeetupProject> findProjects;
        if (cardinal != null) {
            findProjects = meetupRepository.findAllByCardinal(cardinal);
        } else {
            if ("asc".equalsIgnoreCase(order)) {
                findProjects = meetupRepository.findAllByOrderByCardinalAsc();
            } else {
                findProjects = meetupRepository.findAllByOrderByCardinalDesc();
            }
        }

        if ("OB".equalsIgnoreCase(batch)) {
            findProjects = findProjects.stream()
                    .filter(meetupProject -> meetupProject.getMeetupId().equals(55L))
                    .collect(Collectors.toList());
        } else if ("YB".equalsIgnoreCase(batch)) {
            findProjects = findProjects.stream()
                    .filter(meetupProject -> !meetupProject.getMeetupId().equals(55L))
                    .collect(Collectors.toList());
        }

        List<MeetupDetailResponse> meetupDetailResponses = findProjects.stream()
                .map(meetupProject -> {
                    List<String> tags = meetupProject.getTags().stream()
                            .map(tag -> "#" + tag.getName())
                            .collect(Collectors.toList());
                    MeetupDetailResponse response = new MeetupDetailResponse(meetupProject, true);
                    response.setTags(tags);
                    return response;
                })
                .collect(Collectors.toList());

        return MeetupResponse.builder()
                .meetupCount(meetupDetailResponses.size())
                .meetupList(meetupDetailResponses)
                .build();
    }


    public MeetupDetailResponse getMeetupProject(Long meetupId) {
        MeetupProject findProject = meetupRepository.findById(meetupId).orElseThrow();
        return new MeetupDetailResponse(findProject, true);
    }
}
