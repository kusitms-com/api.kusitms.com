package com.kusitms.website.domain.email.repository;

import com.kusitms.website.domain.email.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    boolean existsByEmail(String email);

}
