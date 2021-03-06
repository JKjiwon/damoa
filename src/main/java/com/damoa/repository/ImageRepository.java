package com.damoa.repository;

import com.damoa.domain.post.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete" +
            " from Image i" +
            " where i.post.id = :id")
    void deleteByPostId(@Param("id") Long id);
}
