package com.damoa.service;

import com.damoa.domain.community.Category;
import com.damoa.domain.community.Community;
import com.damoa.domain.community.CommunityMember;
import com.damoa.domain.community.MemberGrade;
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

        category = new Category("??????");

        community = Community.builder()
                .id(1L)
                .name("????????? ??????")
                .introduction("?????? ??????. ?????? ?????? ??? ??? ??????.")
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
                .content("?????? ???????????????.")
                .build();

        member1Comment = new Comment(1L, "?????? ???????????????.", 1, member1, post);

        updateCommentDto = new UpdateCommentDto("???????????????");
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????? ??????")
    public void createCommentWithoutParent_Success() {
        // given
        CreateCommentDto createCommentDto = new CreateCommentDto("??????", null);
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
    @DisplayName("?????? ????????? ?????? ?????? ?????? ??????")
    public void createCommentWithParent_Success() {
        // given
        Comment parent = new Comment(1L, "hello", 1, member1, post);
        CreateCommentDto createCommentDto = new CreateCommentDto("??????", 1L);
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
    @DisplayName("?????? ????????? ?????? ????????? ????????? ?????? ????????? ????????? ?????? ????????? ????????? ????????? ????????? ????????? ??????.")
    public void createCommentWithGrandparent_Success() {
        // given
        Comment grandparent = new Comment(1L, "hello", 1, member1, post);
        Comment parent = new Comment(2L, "hello", 2, member1, post);
        parent.setParent(grandparent);

        CreateCommentDto createCommentDto = new CreateCommentDto("??????", parent.getId());
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
    @DisplayName("?????? ??????????????? ????????? ????????? ???????????? CommunityNotJoinedException ????????? ?????? ??????")
    public void createCommunityWithNotJoinedMember_Fail() {
        // given
        CreateCommentDto createCommentDto = new CreateCommentDto("??????", null);
        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(false);

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> commentService.create(any(), any(), createCommentDto, member1));

        // then
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("?????? ??????????????? ???????????? ???????????? ????????? PostNotFoundException ????????? ?????? ??????")
    public void createCommunityWithNotFoundPost_Fail() {
        // given
        CreateCommentDto createCommentDto = new CreateCommentDto("??????", null);
        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(true);
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class,
                () -> commentService.create(any(), any(), createCommentDto, member1));

        // then
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("????????? ??? ??????????????? ?????? ?????? ??????")
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
    @DisplayName("???????????? ?????? ????????? OWNER?????? ?????? ?????? ??????")
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
    @DisplayName("??????????????? ????????? ?????? ???????????? CommunityNotJoinedException ????????? ?????? ?????? ??????")
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
    @DisplayName("?????? ?????? ????????? ????????? AccessDeniedException ????????? ?????? ?????? ??????")
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
    @DisplayName("????????? ??? ??????????????? ?????? ?????? ??????")
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
    @DisplayName("???????????? ?????? ????????? OWNER????????? ?????? ?????? ??????")
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
    @DisplayName("??????????????? ????????? ?????? ???????????? CommunityNotJoinedException ????????? ?????? ?????? ??????")
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
    @DisplayName("?????? ?????? ????????? ????????? AccessDeniedException ????????? ?????? ?????? ??????")
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
    @DisplayName("?????????Id??? ????????? ?????? ?????? ?????? ??????")
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
    @DisplayName("?????? id??? ?????? ?????? ??????")
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
    @DisplayName("?????? id??? ?????? ?????? ?????? ??? ??? ?????? ????????? ???????????? ????????? CommentNotFoundException ????????? ?????? ??????")
    public void findOneWithAllSubCommentWithNotFoundComment_Fail() {
        // given
        when(commentRepository.findOneWithAll(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommentNotFoundException.class,
                () -> commentService.findOneWithAllSubComment(any(), any())
        );
    }
}