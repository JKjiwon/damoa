package hello.sns.repository;

import hello.sns.entity.post.Image;
import hello.sns.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Image i where i.post = :post")
    void deleteByPost(@Param("post") Post post);
}
