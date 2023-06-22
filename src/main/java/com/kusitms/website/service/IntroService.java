package com.kusitms.website.service;

import com.kusitms.website.dto.intro.response.IntroResponse;
import com.kusitms.website.repository.IntroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IntroService {

    private final IntroRepository introRepository;

    @Transactional(readOnly = true)
    public IntroResponse getIntro() {
        return null;
    }

}
