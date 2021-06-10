package hello.sns.service;

import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import hello.sns.domain.post.Post;
import hello.sns.repository.CommentRepository;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CommentDto;
import hello.sns.web.dto.post.CreateCommentDto;
import hello.sns.web.dto.post.UpdateCommentDto;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.CommentNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinedException;
import hello.sns.web.exception.business.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommunityMemberRepository communityMemberRepository;


    @Override
    @Transactional
    public Long create(Long communityId, CreateCommentDto dto, Member currentMember) {

        // 커뮤니티에 가입된 회원인지 확인
        validateJoinedMember(communityId, currentMember);

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(PostNotFoundException::new);

        Comment parent = dto.existsParentCommentId() ? commentRepository.findById(dto.getParentCommentId())
                .orElseThrow(CommentNotFoundException::new) : null;

        // 최상위 parent
        if (parent != null) {
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
        }

        Comment comment = dto.toEntity(post, parent, currentMember);

        return commentRepository.save(comment).getId();
    }

    @Override
    @Transactional
    public void delete(Long communityId, Long commentId, Member currentMember) {

        // 커뮤니티에 가입된 회원인지 확인
        CommunityMember communityMember = getCommunityMember(communityId, currentMember.getId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        // 삭제 가능한 회원인지 확인
        if (!comment.writtenBy(currentMember) && !communityMember.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed");
        }

        // 자식댓글이 있으면 자식 댓글까지 삭제
        if (comment.existsChild()) {
            commentRepository.deleteByParentId(commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto findById(Long commentId, Member currentMember) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        return new CommentDto(comment);
    }

    @Transactional
    @Override
    public void Update(Long communityId, Long commentId, UpdateCommentDto updateCommentDto, Member currentMember) {
        // 커뮤니티에 가입된 회원인지 확인
        CommunityMember communityMember = getCommunityMember(communityId, currentMember.getId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        // 삭제 가능한 회원인지 확인
        if (!comment.writtenBy(currentMember) && !communityMember.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed");
        }
        comment.update(updateCommentDto.getContent());
    }

    @Override
    public Page<CommentDto> findAll(Long commentId, Member currentMember, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);
        return comments.map(CommentDto::new);
    }

    private void validateJoinedMember(Long communityId, Member currentMember) {
        boolean isJoinedMember = communityMemberRepository
                .existsByMemberIdAndCommunityId(currentMember.getId(), communityId);
        if (!isJoinedMember) {
            throw new CommunityNotJoinedException();
        }
    }

    private CommunityMember getCommunityMember(Long communityId, Long memberId) {
        return communityMemberRepository
                .findByMemberIdAndCommunityId(memberId, communityId)
                .orElseThrow(CommunityNotJoinedException::new);
    }
}
