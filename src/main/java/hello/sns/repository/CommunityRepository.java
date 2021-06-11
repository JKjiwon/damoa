package hello.sns.repository;

import hello.sns.domain.community.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>, SearchCommunityRepository{

    Boolean existsByName(String name);

    @EntityGraph(attributePaths = "category")
    Optional<Community> findById(Long id);

    @EntityGraph(attributePaths = "category")
    Page<Community> findAll(Pageable pageable);
}
