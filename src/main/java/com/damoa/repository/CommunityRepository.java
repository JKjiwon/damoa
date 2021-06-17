package com.damoa.repository;

import com.damoa.domain.community.Community;
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
