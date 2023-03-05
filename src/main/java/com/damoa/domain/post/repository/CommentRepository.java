package com.damoa.domain.post.repository;

import com.damoa.domain.post.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete" +
            " from Comment c" +
            " where c.parent.id = :id")
    void deleteByParentId(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete" +
            " from Comment c" +
            " where c.id = :id")
    void deleteById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"writer", "parent", "post"})
    Page<Comment> findByPostIdAndLevelOrderByIdDesc(Long postId, Integer level, Pageable pageable);

    List<Comment> findByPostIdAndLevel(Long postId, Integer level);
}
