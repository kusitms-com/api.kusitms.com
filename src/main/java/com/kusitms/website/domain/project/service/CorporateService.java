package com.kusitms.website.domain.project.service;

import com.kusitms.website.domain.project.TagRepository;
import com.kusitms.website.domain.project.entity.CorporateProject;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.CorporateRepository;
import com.kusitms.website.domain.project.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CorporateService {
    private final CorporateRepository corporateRepository;
    private final TagRepository tagRepository;

    public CorporateResponse getCorporateProjects(String order, Integer cardinal) {
        List<CorporateProject> findProjects;
        if (cardinal != null) {
            // If a cardinal is provided, filter projects by the given cardinal.
            findProjects = corporateRepository.findAllByCardinal(cardinal);
        } else {
            // Otherwise, sort all projects by cardinal in the specified order.
            if ("asc".equalsIgnoreCase(order)) {
                findProjects = corporateRepository.findAllByOrderByCardinalAsc();  // Oldest first
            } else {
                findProjects = corporateRepository.findAllByOrderByCardinalDesc(); // Newest first
            }
        }

        List<CorporateDetailResponse> detailResponses = findProjects.stream()
                .map(p -> {
                    List<String> tags = p.getTags().stream()
                            .map(tag -> "#" + tag.getName()) // Extract tag names
                            .collect(Collectors.toList());
                    CorporateDetailResponse response = new CorporateDetailResponse(p);
                    response.setTags(tags); // Set tags in response
                    return response;
                })
                .collect(Collectors.toList());

        return CorporateResponse.builder()
                .corporateCount(findProjects.size())
                .corporateList(detailResponses)
                .build();
    }
}
