package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.web.dto.post.CommentDto;
import hello.sns.web.dto.post.CreateCommentDto;
import hello.sns.web.dto.post.UpdateCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Long create(Long communityId, CreateCommentDto createCommentDto, Member currentMember);

    void delete(Long communityId, Long commentId, Member currentMember);

    CommentDto findById(Long commentId, Member currentMember);

    Page<CommentDto> findAll(Long commentId, Member currentMember, Pageable pageable);

    void Update(Long communityId, Long commentId, UpdateCommentDto updateCommentDto, Member currentMember);
}
