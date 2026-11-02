package com.kusitms.website.domain.project.repository;

import com.kusitms.website.domain.project.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name); // Fetch a tag by name
}
