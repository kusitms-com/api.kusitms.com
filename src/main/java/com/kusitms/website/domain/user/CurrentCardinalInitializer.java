package com.kusitms.website.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 현재 기수 행이 없을 때만 member.initial-cardinal 값으로 생성한다. (최초 배포/빈 DB용)
 * 이후 기수 변경은 어드민 API(PUT /admin/current-cardinal)로 한다.
 */
@Slf4j
@Component
// AdminAuthorityInitializer 등 현재 기수를 읽는 초기화보다 먼저 실행한다
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class CurrentCardinalInitializer implements ApplicationRunner {

    private final CurrentCardinalRepository currentCardinalRepository;

    @Value("${member.initial-cardinal:33}")
    private Integer initialCardinal;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (currentCardinalRepository.count() > 0) {
            return;
        }
        currentCardinalRepository.save(new CurrentCardinal(initialCardinal));
        log.warn("현재 기수 행이 없어 초기값으로 생성: cardinal={}", initialCardinal);
    }
}
