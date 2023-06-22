package com.kusitms.website.domain.introduction;

import com.kusitms.website.domain.introduction.dto.response.IntroResponse;
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
