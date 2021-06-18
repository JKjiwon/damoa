package com.damoa.service;

import com.damoa.domain.community.CommunityMember;
import com.damoa.domain.member.Member;
import com.damoa.domain.post.Comment;
import com.damoa.domain.post.Post;
import com.damoa.repository.CommentRepository;
import com.damoa.repository.CommunityMemberRepository;
import com.damoa.repository.PostRepository;
import com.damoa.web.dto.post.CommentDto;
import com.damoa.web.dto.post.CommentListDto;
import com.damoa.web.dto.post.CreateCommentDto;
import com.damoa.web.dto.post.UpdateCommentDto;
import com.damoa.web.exception.AccessDeniedException;
import com.damoa.web.exception.business.CommentNotFoundException;
import com.damoa.web.exception.business.CommunityNotJoinedException;
import com.damoa.web.exception.business.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final CommunityMemberRepository communityMemberRepository;


    @Override
    @Transactional
    public CommentDto create(Long communityId, Long postId, CreateCommentDto dto, Member currentMember) {
        checkJoinedMember(currentMember, communityId);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment parent = dto.existsParentCommentId() ? commentRepository.findOneWithParent(dto.getParentCommentId())
                .orElseThrow(CommentNotFoundException::new) : null;

        Comment comment = dto.toEntity(post, parent, currentMember);

        return new CommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long communityId, Long postId, Long commentId, Member currentMember){
        CommunityMember actor = getMembership(currentMember.getId(), communityId);

        Comment comment = commentRepository.findOneWithWriter(commentId, postId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.writtenBy(currentMember) && !actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed member");
        }

        commentRepository.deleteByParentId(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public CommentDto update(Long communityId, Long postId, Long commentId, UpdateCommentDto dto, Member currentMember) {
        CommunityMember actor = getMembership(currentMember.getId(), communityId);
        Comment comment = commentRepository.findOneWithWriter(commentId, postId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.writtenBy(currentMember) && !actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed member");
        }
        comment.update(dto.getContent());
        return new CommentDto(comment);
    }

    @Override
    public Page<CommentListDto> findAllByPostId(Long postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostIdAndLevelOrderByIdDesc(postId, 1, pageable);
        return comments.map(CommentListDto::new);
    }

    @Override
    public CommentDto findOneWithAllSubComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findOneWithAll(postId, commentId)
                .orElseThrow(CommentNotFoundException::new);
        return new CommentDto(comment);
    }


    private void checkJoinedMember(Member member, Long communityId) {
        boolean isJoinedMember = communityMemberRepository
                .existsByMemberAndCommunityId(member, communityId);
        if (!isJoinedMember) {
            throw new CommunityNotJoinedException();
        }
    }

    private CommunityMember getMembership(Long memberId, Long communityId) {
        return communityMemberRepository
                .findByMemberIdAndCommunityId(memberId, communityId)
                .orElseThrow(CommunityNotJoinedException::new);
    }
}
