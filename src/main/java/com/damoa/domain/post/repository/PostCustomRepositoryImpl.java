package com.damoa.domain.post.repository;

import com.damoa.domain.community.QCommunity;
import com.damoa.domain.post.entity.Post;
import com.damoa.domain.post.QPost;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

public class PostCustomRepositoryImpl extends QuerydslRepositorySupport implements PostCustomRepository {

    private static final QPost post = QPost.post;
    private static final QCommunity community = QCommunity.community;

    public PostCustomRepositoryImpl() {
        super(Post.class);
    }

    @Override
    public Optional<Post> findByIdWithAll(Long postId) {
        Post queryResult = from(post)
                .join(post.writer).fetchJoin()
                .join(post.community, community).fetchJoin()
                .leftJoin(post.images)
                .where(
                        post.id.eq(postId)
                ).fetchOne();

        return Optional.ofNullable(queryResult);
    }

    @Override
    public Optional<Post> findByIdAndCommunityId(Long postId, Long communityId) {
        Post queryResult = from(PostCustomRepositoryImpl.post)
                .join(PostCustomRepositoryImpl.post.writer).fetchJoin()
                .join(PostCustomRepositoryImpl.post.community, community).fetchJoin()
                .leftJoin(PostCustomRepositoryImpl.post.images).fetchJoin()
                .where(
                        PostCustomRepositoryImpl.post.id.eq(postId),
                        community.id.eq(communityId)
                ).fetchOne();

        return Optional.ofNullable(queryResult);
    }
}
