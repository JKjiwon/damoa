package hello.sns.repository;

import hello.sns.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select distinct p" +
            " from Post p" +
            " join fetch p.writer" +
            " join fetch p.community c" +
            " left outer join fetch p.images" +
            " where p.id = :postId")
    Optional<Post> findByIdWithAll(@Param("postId") Long postId);

    @Query("select distinct p" +
            " from Post p" +
            " join fetch p.writer" +
            " join fetch p.community c" +
            " left outer join fetch p.images" +
            " where p.id = :postId and c.id = :communityId")
    Optional<Post> findByIdAndCommunityId(@Param("postId") Long postId, @Param("communityId") Long communityId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete" +
            " from Post p" +
            " where p.id = :id")
    void deleteById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"writer", "community"})
    Page<Post> findAllByCommunityIdOrderByIdDesc(Long communityId, Pageable pageable);
}
