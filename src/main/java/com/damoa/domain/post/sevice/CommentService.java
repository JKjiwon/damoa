package com.damoa.domain.post.sevice;

import com.damoa.domain.member.entity.Member;
import com.damoa.domain.post.dto.CommentDto;
import com.damoa.domain.post.dto.CommentListDto;
import com.damoa.domain.post.dto.CreateCommentDto;
import com.damoa.domain.post.dto.UpdateCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentDto create(Long communityId, Long postId, CreateCommentDto dto, Member currentMember);

    void delete(Long communityId, Long postId, Long commentId, Member currentMember);

    CommentDto update(Long communityId, Long postId, Long commentId,
                      UpdateCommentDto dto, Member currentMember);

    Page<CommentListDto> findAllByPostId(Long postId, Pageable pageable);

    CommentDto findOneWithAllSubComment(Long postId, Long commentId);
}
