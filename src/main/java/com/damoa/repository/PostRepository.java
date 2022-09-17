package com.damoa.repository;

import com.damoa.domain.community.Community;
import com.damoa.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete" +
            " from Post p" +
            " where p.id = :id")
    void deleteById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"writer", "community"})
    Page<Post> findAllByCommunityIdOrderByIdDesc(Long communityId, Pageable pageable);

    @EntityGraph(attributePaths = {"writer", "community"})
    Page<Post> findByCommunityInOrderByIdDesc(Pageable pageable, List<Community> communities);
}
