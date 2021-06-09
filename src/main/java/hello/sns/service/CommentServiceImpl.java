package hello.sns.service;

import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Comment;
import hello.sns.entity.post.Post;
import hello.sns.repository.CommentRepository;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CreateCommentDto;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.CommentAlreadyDeletedException;
import hello.sns.web.exception.business.CommentNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinedException;
import hello.sns.web.exception.business.PostNotFoundException;
import lombok.RequiredArgsConstructor;
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
    public void create(Long communityId, CreateCommentDto dto, Member currentMember) {

        // 커뮤니티에 가입된 회원인지 확인
        validateJoinedMember(communityId, currentMember);

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(PostNotFoundException::new);

        Comment parent = null;
        if (dto.existsParentCommentId()) {
            parent = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(CommentNotFoundException::new);
        }

        Comment comment = dto.toEntity(post, parent, currentMember);

        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(Long communityId, Long commentId, Member currentMember) {

        // 커뮤니티에 가입된 회원인지 확인
        CommunityMember communityMember = getCommunityMember(communityId, currentMember.getId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (comment.isHidden()) {
            throw new CommentAlreadyDeletedException();
        }

        // 삭제 가능한 회원인지 확인
        if (!comment.writtenBy(currentMember) && !communityMember.isOwnerOrAdmin()) {
            throw new AccessDeniedException();
        }

        // 자식댓글이 있으면 숨김 처리, 자식 댓글이 없으면 삭제
        if (comment.existsChildren()) {
            comment.setHidden(true);
        } else {
            commentRepository.delete(comment);
        }
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
