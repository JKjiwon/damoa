package hello.sns.repository;

import hello.sns.domain.post.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c" +
            " where c.parent.id = :id")
    void deleteByParentId(@Param("id") Long id);


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.id = :id")
    void deleteById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"child", "parent", "writer"})
    Optional<Comment> findById(Long commentId);
}
