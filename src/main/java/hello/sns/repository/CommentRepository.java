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
    @Query("delete" +
            " from Comment c" +
            " where c.parent.id = :id")
    void deleteByParentId(@Param("id") Long id);


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete" +
            " from Comment c" +
            " where c.id = :id")
    void deleteById(@Param("id") Long id);

    @Query("select c" +
            " from Comment c" +
            " join fetch c.parent" +
            " where c.id = :commentId")
    Optional<Comment> findOneWithParent(@Param("commentId") Long commentId);

    @Query("select c" +
            " from Comment c" +
            " join fetch c.writer" +
            " join fetch c.child" +
            " where c.id = :commentId and c.post.id = :postId")
    Optional<Comment> findOneWithWriterAndChild(
            @Param("postId") Long postId,
            @Param("commentId") Long commentId);

    @Query("select c" +
            " from Comment c" +
            " join fetch c.writer" +
            " join fetch c.child" +
            " join fetch c.parent " +
            " join fetch c.post" +
            " where c.id = :commentId and c.post.id = :postId")
    Optional<Comment>findOneWithAll(
            @Param("postId") Long postId,
            @Param("commentId") Long commentId);
}
