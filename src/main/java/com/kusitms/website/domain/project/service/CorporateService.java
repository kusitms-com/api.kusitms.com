package com.kusitms.website.domain.project.service;

import com.kusitms.website.domain.project.entity.CorporateProject;
import com.kusitms.website.domain.project.dto.response.CorporateDetailResponse;
import com.kusitms.website.domain.project.dto.response.CorporateResponse;
import com.kusitms.website.domain.project.CorporateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorporateService {
    private final CorporateRepository corporateRepository;

    @Transactional(readOnly = true)
    public CorporateResponse getCorporateProjects() {
        List<CorporateProject> findProjects = corporateRepository.findAllByOrderByCardinalDesc();

        List<CorporateDetailResponse> detailResponses = findProjects.stream()
                .map(p -> new CorporateDetailResponse(p))
                .collect(Collectors.toList());

        return CorporateResponse.builder()
                .corporateCount(findProjects.size())
                .corporateList(detailResponses)
                .build();
    }
}
