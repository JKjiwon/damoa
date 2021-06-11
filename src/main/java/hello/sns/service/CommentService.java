package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.web.dto.post.CommentDto;
import hello.sns.web.dto.post.CommentListDto;
import hello.sns.web.dto.post.CreateCommentDto;
import hello.sns.web.dto.post.UpdateCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Long create(Long communityId, Long postId, CreateCommentDto createCommentDto, Member currentMember);

    void delete(Long communityId, Long postId, Long commentId, Member currentMember);

    void update(Long communityId, Long commentId, UpdateCommentDto updateCommentDto, Member currentMember);

    Page<CommentListDto> findAllByPostId(Long postId, Member currentMember, Pageable pageable);

    CommentDto findOneWithAll(Long postId, Long commentId, Member currentMember);
}
