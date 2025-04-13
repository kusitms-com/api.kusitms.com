package com.kusitms.website.domain.project.service;

import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.project.MeetupRepository;
import com.kusitms.website.domain.project.MeetupTeamRepository;
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
    private final S3Service s3Service;
    private final MeetupRepository meetupRepository;
    private final MeetupTeamRepository meetupTeamRepository;

    public MeetupResponse getMeetupProjects(String order, Integer cardinal) {
        List<MeetupProject> findProjects;
        if (cardinal != null) {
            // If a cardinal is provided, filter projects by the given cardinal value.
            findProjects = meetupRepository.findAllByCardinal(cardinal);
        } else {
            // Otherwise, sort all projects by cardinal in the specified order.
            if ("asc".equalsIgnoreCase(order)) {
                findProjects = meetupRepository.findAllByOrderByCardinalAsc();  // Oldest first
            } else {
                findProjects = meetupRepository.findAllByOrderByCardinalDesc(); // Newest first
            }
        }

        List<MeetupDetailResponse> meetupDetailResponses = findProjects.stream()
                .map(p -> {
                    List<String> tags = p.getTags().stream()
                            .map(tag -> "#" + tag.getName()) // Extract tag names
                            .collect(Collectors.toList());
                    MeetupDetailResponse response = new MeetupDetailResponse(p, false);
                    response.setTags(tags); // Set tags in response
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
