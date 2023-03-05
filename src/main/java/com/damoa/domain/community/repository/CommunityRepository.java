package com.damoa.domain.community.repository;

import com.damoa.domain.community.entity.Community;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>, SearchCommunityRepository{

    Boolean existsByName(String name);

    @EntityGraph(attributePaths = "category")
    Optional<Community> findById(Long id);
}
