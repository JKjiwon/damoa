package hello.sns.service;

import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import hello.sns.domain.post.Post;
import hello.sns.repository.CommentRepository;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CommentDto;
import hello.sns.web.dto.post.CommentListDto;
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

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommunityMemberRepository communityMemberRepository;


    @Override
    @Transactional
    public Long create(Long communityId, Long postId, CreateCommentDto dto, Member currentMember) {

        checkJoinedMember(currentMember, communityId);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment parent = dto.existsParentCommentId() ? commentRepository.findOneWithParent(dto.getParentCommentId())
                .orElseThrow(CommentNotFoundException::new) : null;

        Comment comment = dto.toEntity(post, parent, currentMember);

        return commentRepository.save(comment).getId();
    }

    @Override
    @Transactional
    public void delete(Long communityId, Long postId, Long commentId, Member currentMember){

        // 커뮤니티에 가입된 회원인지 확인
        CommunityMember actor = getMembership(currentMember.getId(), communityId);

        Comment comment = commentRepository.findOneWithWriter(commentId, postId)
                .orElseThrow(CommentNotFoundException::new);

        // 삭제 가능한 회원인지 확인
        if (!comment.writtenBy(currentMember) && !actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed member");
        }

        // 자식댓글이 있으면 자식 댓글까지 삭제
        commentRepository.deleteByParentId(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public void update(Long communityId, Long postId, Long commentId, UpdateCommentDto updateCommentDto, Member currentMember) {
        // 커뮤니티에 가입된 회원인지 확인
        CommunityMember actor = getMembership(currentMember.getId(), communityId);

        Comment comment = commentRepository.findOneWithWriter(commentId, postId)
                .orElseThrow(CommentNotFoundException::new);

        // 삭제 가능한 회원인지 확인
        if (!comment.writtenBy(currentMember) && !actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed member");
        }
        comment.update(updateCommentDto.getContent());
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
