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

    @EntityGraph(attributePaths = {"writer", "community", "images"})
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = {"writer", "community", "images"})
    Optional<Post> findByIdAndCommunityId(Long postId, Long communityId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Post p where p.id = :id")
    void deleteById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"writer", "community"})
    Page<Post> findAllByCommunityId(Long communityId, Pageable pageable);
}
