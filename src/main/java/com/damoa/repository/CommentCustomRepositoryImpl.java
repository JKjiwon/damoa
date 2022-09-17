package com.damoa.repository;

import com.damoa.domain.post.Comment;
import com.damoa.domain.post.QComment;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

public class CommentCustomRepositoryImpl extends QuerydslRepositorySupport implements CommentCustomRepository {

    private static final QComment comment = QComment.comment;
    private static final QComment parentComment = QComment.comment;

    public CommentCustomRepositoryImpl() {
        super(Comment.class);
    }

    @Override
    public Optional<Comment> findOneWithParent(Long commentId) {
        Comment queryResult = from(comment)
                .leftJoin(comment.parent, parentComment).fetchJoin()
                .where(parentComment.id.eq(commentId))
                .fetchOne();

        return Optional.ofNullable(queryResult);
    }

    @Override
    public Optional<Comment> findOneWithWriter(Long commentId, Long postId) {
        Comment queryResult = from(comment)
                .join(comment.writer).fetchJoin()
                .where(
                        comment.id.eq(commentId),
                        comment.post.id.eq(postId)
                )
                .fetchOne();
        return Optional.ofNullable(queryResult);
    }

    @Override
    public Optional<Comment> findOneWithAll(Long postId, Long commentId) {
        Comment queryResult = from(comment)
                .join(comment.writer).fetchJoin()
                .join(comment.post).fetchJoin()
                .leftJoin(comment.parent).fetchJoin()
                .leftJoin(comment.child).fetchJoin()
                .where(
                        comment.id.eq(commentId),
                        comment.post.id.eq(postId)
                )
                .fetchOne();

        return Optional.ofNullable(queryResult);
    }
}
