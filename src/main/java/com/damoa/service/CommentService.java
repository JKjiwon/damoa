package com.damoa.service;

import com.damoa.domain.member.Member;
import com.damoa.web.dto.post.CommentDto;
import com.damoa.web.dto.post.CommentListDto;
import com.damoa.web.dto.post.CreateCommentDto;
import com.damoa.web.dto.post.UpdateCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentDto create(Long communityId, Long postId, CreateCommentDto createCommentDto, Member currentMember);

    void delete(Long communityId, Long postId, Long commentId, Member currentMember);

    CommentDto update(Long communityId, Long postId, Long commentId,
                UpdateCommentDto updateCommentDto, Member currentMember);

    Page<CommentListDto> findAllByPostId(Long postId, Pageable pageable);

    CommentDto findOneWithAllSubComment(Long postId, Long commentId);
}
