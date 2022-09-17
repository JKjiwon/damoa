package com.damoa.repository;

import com.damoa.domain.post.Post;

import java.util.Optional;

public interface PostCustomRepository {

    Optional<Post> findByIdWithAll(Long postId);

    Optional<Post> findByIdAndCommunityId(Long postId, Long communityId);
}
