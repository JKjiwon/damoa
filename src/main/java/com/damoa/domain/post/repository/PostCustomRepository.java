package com.damoa.domain.post.repository;

import com.damoa.domain.post.entity.Post;

import java.util.Optional;

public interface PostCustomRepository {

    Optional<Post> findByIdWithAll(Long postId);

    Optional<Post> findByIdAndCommunityId(Long postId, Long communityId);
}
