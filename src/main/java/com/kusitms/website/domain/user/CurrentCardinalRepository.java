package com.kusitms.website.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface CurrentCardinalRepository extends JpaRepository<CurrentCardinal, Long> {
    // 현재 기수는 단일 행으로 관리한다
    Optional<CurrentCardinal> findTopByOrderByIdAsc();

    // 기수 변경이 동시에 들어와도 한 번에 하나씩 처리되도록 행 잠금을 건다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CurrentCardinal c order by c.id asc")
    List<CurrentCardinal> findAllForUpdate();
}
