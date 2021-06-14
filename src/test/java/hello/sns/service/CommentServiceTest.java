package hello.sns.service;

import hello.sns.domain.community.Category;
import hello.sns.domain.community.Community;
import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.community.MemberGrade;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class CommentServiceTest {

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommunityMemberRepository communityMemberRepository;

    Member owner;
    Member member1;
    Member member2;
    Category category;
    Community community;
    CommunityMember ownerMembership;
    CommunityMember member1Membership;
    CommunityMember member2Membership;
    Post post;
    Comment member1Comment;
    UpdateCommentDto updateCommentDto;

    @BeforeEach
    void init() {
        owner = Member.builder()
                .id(1L)
                .name("owner")
                .email("owner@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        member1 = Member.builder()
                .id(2L)
                .name("member")
                .email("member@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        member2 = Member.builder()
                .id(3L)
                .name("member2")
                .email("member2@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        category = new Category("운동");

        community = Community.builder()
                .id(1L)
                .name("다모아 수영")
                .introduction("일단 와바. 수영 너도 할 수 있어.")
                .owner(owner)
                .category(category)
                .build();

        community.join(owner, MemberGrade.OWNER);
        community.join(member1, MemberGrade.USER);
        community.join(member2, MemberGrade.USER);

        ownerMembership = new CommunityMember(community, owner, MemberGrade.OWNER);
        member1Membership = new CommunityMember(community, member1, MemberGrade.USER);
        member2Membership = new CommunityMember(community, member2, MemberGrade.USER);

        post = Post.builder()
                .content("함께 운동합시다.")
                .build();

        member1Comment = new Comment(1L, "저도 동참합니다.", 1, member1, post);

        updateCommentDto = new UpdateCommentDto("안녕하세요");
    }

    @Test
    @DisplayName("부모 댓글이 없는 댓글 등록 성공")
    public void createCommentWithoutParent_Success() {
        // given
        CreateCommentDto createCommentDto = new CreateCommentDto("안녕", null);
        Comment comment = createCommentDto.toEntity(post, null, member1);

        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(true);
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(commentRepository.save(any())).thenReturn(comment);

        // when
        commentService.create(any(), any(), createCommentDto, member1);

        // then
        verify(commentRepository).save(any());
        assertThat(comment.getLevel()).isEqualTo(1);
    }

    @Test
    @DisplayName("부모 댓글이 있는 댓글 등록 성공")
    public void createCommentWithParent_Success() {
        // given
        Comment parent = new Comment(1L, "hello", 1, member1, post);
        CreateCommentDto createCommentDto = new CreateCommentDto("안녕", 1L);
        Comment comment = createCommentDto.toEntity(post, parent, member2);

        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(true);
        when(commentRepository.findOneWithParent(any())).thenReturn(Optional.ofNullable(parent));
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(commentRepository.save(any())).thenReturn(comment);

        // when
        commentService.create(any(), any(), createCommentDto, member2);

        // then
        verify(commentRepository).save(any());
        assertThat(comment.getLevel()).isEqualTo(2);
        assertThat(comment.getParent().getId()).isEqualTo(parent.getId());
    }

    @Test
    @DisplayName("부모 댓글이 있는 댓글을 부모로 하여 댓글을 작성할 경우 등록한 댓글은 최상위 부모에 속하게 된다.")
    public void createCommentWithGrandparent_Success() {
        // given
        Comment grandparent = new Comment(1L, "hello", 1, member1, post);
        Comment parent = new Comment(2L, "hello", 2, member1, post);
        parent.setParent(grandparent);

        CreateCommentDto createCommentDto = new CreateCommentDto("안녕", parent.getId());
        Comment comment = createCommentDto.toEntity(post, parent, member2);

        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(true);
        when(commentRepository.findOneWithParent(any())).thenReturn(Optional.ofNullable(parent));
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(commentRepository.save(any())).thenReturn(comment);

        // when
        commentService.create(any(), any(), createCommentDto, member2);

        // then
        verify(commentRepository).save(any());
        assertThat(comment.getLevel()).isEqualTo(2);
        assertThat(comment.getParent().getId()).isEqualTo(grandparent.getId());
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원이 아니라면 CommunityNotJoinedException 던지며 등록 실패")
    public void createCommunityWithNotJoinedMember_Fail() {
        // given
        CreateCommentDto createCommentDto = new CreateCommentDto("안녕", null);
        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(false);

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> commentService.create(any(), any(), createCommentDto, member1));

        // then
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("해당 커뮤니티에 게시글이 존재하지 않으면 PostNotFoundException 던지며 등록 실패")
    public void createCommunityWithNotFoundPost_Fail() {
        // given
        CreateCommentDto createCommentDto = new CreateCommentDto("안녕", null);
        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(true);
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class,
                () -> commentService.create(any(), any(), createCommentDto, member1));

        // then
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("자신이 쓴 댓글이라면 댓글 삭제 성공")
    public void deleteCommentWithWriter_Success() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));
        when(commentRepository.findOneWithWriter(any(), any())).thenReturn(Optional.ofNullable(member1Comment));

        // when
        commentService.delete(any(), any(), member1Comment.getId(), member1);

        // then
        verify(commentRepository).deleteByParentId(member1Comment.getId());
        verify(commentRepository).deleteById(member1Comment.getId());
    }

    @Test
    @DisplayName("커뮤니티 회원 등급이 OWNER라면 댓글 삭제 성공")
    public void deleteCommentWithOwner_Success() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(ownerMembership));
        when(commentRepository.findOneWithWriter(any(), any())).thenReturn(Optional.ofNullable(member1Comment));

        // when
        commentService.delete(any(), any(), member1Comment.getId(), owner);

        // then
        verify(commentRepository).deleteByParentId(member1Comment.getId());
        verify(commentRepository).deleteById(member1Comment.getId());
    }

    @Test
    @DisplayName("커뮤니티에 가입된 회원 아니라면 CommunityNotJoinedException 던지며 댓글 삭제 실패")
    public void deleteCommentWithNotJoinedMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> commentService.delete(any(), any(), member1Comment.getId(), member2));

        // then
        verify(commentRepository, times(0)).deleteByParentId(member1Comment.getId());
        verify(commentRepository, times(0)).deleteById(member1Comment.getId());
    }

    @Test
    @DisplayName("댓글 삭제 권한이 없으면 AccessDeniedException 던지며 댓글 삭제 실패")
    public void deleteCommentWithNotAccess_Fail() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member2Membership));
        when(commentRepository.findOneWithWriter(any(), any())).thenReturn(Optional.ofNullable(member1Comment));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> commentService.delete(any(), any(), member1Comment.getId(), member2));

        // then
        verify(commentRepository, times(0)).deleteByParentId(member1Comment.getId());
        verify(commentRepository, times(0)).deleteById(member1Comment.getId());
    }


    @Test
    @DisplayName("자신이 쓴 댓글이라면 댓글 수정 성공")
    public void updateCommentWithWriter_Success() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));
        when(commentRepository.findOneWithWriter(any(), any())).thenReturn(Optional.ofNullable(member1Comment));

        // when
        commentService.update(any(), any(), member1Comment.getId(), updateCommentDto, member1);

        // then
        assertThat(member1Comment.getContent()).isEqualTo(updateCommentDto.getContent());
    }

    @Test
    @DisplayName("커뮤니티 회원 등급이 OWNER이라면 댓글 수정 성공")
    public void updateCommentWithOwner_Success() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(ownerMembership));
        when(commentRepository.findOneWithWriter(any(), any())).thenReturn(Optional.ofNullable(member1Comment));

        // when
        commentService.update(any(), any(), member1Comment.getId(), updateCommentDto, owner);

        // then
        assertThat(member1Comment.getContent()).isEqualTo(updateCommentDto.getContent());
    }

    @Test
    @DisplayName("커뮤니티에 가입된 회원 아니라면 CommunityNotJoinedException 던지며 댓글 수정 실패")
    public void updateCommentWithNotJoinedMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> commentService.update(any(), any(), member1Comment.getId(), updateCommentDto, member2));

        // then
        assertThat(member1Comment.getContent()).isNotEqualTo(updateCommentDto.getContent());
    }

    @Test
    @DisplayName("댓글 수정 권한이 없으면 AccessDeniedException 던지며 댓글 수정 실패")
    public void updateCommentWithNotAccess_Fail() {
        // given
        when(communityMemberRepository.findByMemberIdAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member2Membership));
        when(commentRepository.findOneWithWriter(any(), any())).thenReturn(Optional.ofNullable(member1Comment));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> commentService.update(any(), any(), member1Comment.getId(), updateCommentDto, member2));

        // then
        assertThat(member1Comment.getContent()).isNotEqualTo(updateCommentDto.getContent());
    }

    @Test
    @DisplayName("게시글Id로 관련된 모든 부모 댓글 조회")
    public void findAllByPostId_Success() {
        // given
        Comment comment1 = new Comment(1L, "comment1", 1, member1, post);
        Comment comment2 = new Comment(2L, "comment2", 1, member1, post);
        Comment comment3 = new Comment(3L, "comment3", 2, member1, post);
        Comment comment4 = new Comment(4L, "comment4", 2, member1, post);
        comment3.setParent(comment1);
        comment4.setParent(comment2);
        comment1.setCreatedAt(LocalDateTime.now());
        comment2.setCreatedAt(LocalDateTime.now());
        comment3.setCreatedAt(LocalDateTime.now());
        comment4.setCreatedAt(LocalDateTime.now());

        List<Comment> parentComments = List.of(comment1, comment2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        when(commentRepository.findByPostIdAndLevelOrderByIdDesc(post.getId(), 1, pageable))
                .thenReturn(new PageImpl<>(parentComments));

        // when
        Page<CommentListDto> CommentListDtos = commentService.findAllByPostId(post.getId(), pageable);

        // then
        assertThat(CommentListDtos.getSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글 id로 댓글 단건 조회")
    public void findOneWithAllSubComment_Success() {
        // given
        Comment comment1 = new Comment(1L, "comment1", 1, member1, post);
        Comment comment2 = new Comment(2L, "comment2", 1, member1, post);
        Comment comment3 = new Comment(3L, "comment3", 2, member1, post);
        Comment comment4 = new Comment(4L, "comment4", 2, member1, post);
        comment2.setParent(comment1);
        comment3.setParent(comment1);
        comment4.setParent(comment1);
        comment1.setCreatedAt(LocalDateTime.now());
        comment2.setCreatedAt(LocalDateTime.now());
        comment3.setCreatedAt(LocalDateTime.now());
        comment4.setCreatedAt(LocalDateTime.now());

        when(commentRepository.findOneWithAll(any(), any())).thenReturn(Optional.ofNullable(comment1));

        // when
        CommentDto commentDto = commentService.findOneWithAllSubComment(any(), any());

        // then
        assertThat(commentDto.getSubComments().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글 id로 댓글 단건 조회 할 때 해당 댓글이 존재하지 않으면 CommentNotFoundException 던지며 조회 실패")
    public void findOneWithAllSubCommentWithNotFoundComment_Fail() {
        // given
        when(commentRepository.findOneWithAll(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommentNotFoundException.class,
                () -> commentService.findOneWithAllSubComment(any(), any())
        );
    }
}