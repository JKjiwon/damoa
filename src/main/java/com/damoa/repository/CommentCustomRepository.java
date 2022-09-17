package com.damoa.repository;

import com.damoa.domain.post.Comment;

import java.util.Optional;

public interface CommentCustomRepository {

    Optional<Comment> findOneWithParent(Long commentId);

    Optional<Comment> findOneWithWriter(Long commentId, Long postId);

    Optional<Comment> findOneWithAll(Long postId, Long commentId);
}
