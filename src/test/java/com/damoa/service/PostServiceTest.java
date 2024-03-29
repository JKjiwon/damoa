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
import com.damoa.repository.ImageRepository;
import com.damoa.repository.PostRepository;
import com.damoa.web.dto.post.CreatePostDto;
import com.damoa.web.dto.post.PostDto;
import com.damoa.web.dto.post.PostUploadImage;
import com.damoa.web.exception.AccessDeniedException;
import com.damoa.web.exception.business.CommunityNotJoinedException;
import com.damoa.web.exception.business.FileUploadException;
import com.damoa.web.exception.business.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class PostServiceTest {

    @InjectMocks
    PostServiceImpl postService;

    @Mock
    PostRepository postRepository;

    @Mock
    CommunityMemberRepository communityMemberRepository;

    @Mock
    FileService fileService;

    @Mock
    ImageRepository imageRepository;

    @Mock
    CommentRepository commentRepository;

    Member owner;
    Member member1;
    Member member2;
    Category category;
    Community community;
    CommunityMember ownerMembership;
    CommunityMember member1Membership;
    CommunityMember member2Membership;
    CreatePostDto createPostDto;
    MockMultipartFile image;
    PostUploadImage postUploadImage;
    Post post;

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

        ownerMembership = new CommunityMember(community, owner, MemberGrade.OWNER);
        member1Membership = new CommunityMember(community, member1, MemberGrade.USER);
        member2Membership = new CommunityMember(community, member2, MemberGrade.USER);

        createPostDto = new CreatePostDto("배영 기록");

        image = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        post = createPostDto.toEntity(member1, community);

        postUploadImage = new PostUploadImage("hello", "2021/06/07/hello", 1);
    }

    @Test
    @DisplayName("이미지가 없고, 필수 입력값(title, content)가 주어졌을 경우 게시글 등록 성공")
    public void createPostWithoutImage_Success() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));
        when(postRepository.save(any())).thenReturn(post);

        // when
        postService.create(community.getId(), member1, createPostDto, null);

        // then
        verify(postRepository).save(any());
        verify(communityMemberRepository).findByMemberAndCommunityId(member1, community.getId());
        verify(fileService, times(0)).storePostImages(any());
    }

    @Test
    @DisplayName("이미지가 비어있고, 필수 입력값(title, content)가 주어졌을 경우 게시글 등록 성공")
    public void createPostWithEmptyImage_Success() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));
        when(postRepository.save(any())).thenReturn(post);

        // when
        postService.create(community.getId(), member1, createPostDto, List.of());

        // then
        verify(postRepository).save(any());
        verify(communityMemberRepository).findByMemberAndCommunityId(member1, community.getId());
        verify(fileService, times(0)).storePostImages(any());
    }

    @Test
    @DisplayName("이미지가 2개 있고, 필수 입력값(title, content)가 주어졌을 경우 게시글 등록 성공")
    public void createPostWithTwoImages_Success() {
        // given
        List<MultipartFile> imageFiles = List.of(image, image);
        List<PostUploadImage> postUploadImages = List.of(postUploadImage, postUploadImage);

        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));
        when(postRepository.save(any())).thenReturn(post);
        when(fileService.storePostImages(imageFiles)).thenReturn(postUploadImages);

        // when
        postService.create(community.getId(), member1, createPostDto, imageFiles);

        // then
        verify(postRepository).save(any());
        verify(communityMemberRepository).findByMemberAndCommunityId(member1, community.getId());
        verify(fileService, times(1)).storePostImages(imageFiles);
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원이 아니면 CommunityNotJoinException를 던지며 게시글 등록 실패")
    public void createCommunityWithNotJoinedMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> postService.create(community.getId(), member1, createPostDto, null));

        // then
        verify(postRepository, times(0)).save(any());
    }

    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 게시글 등록 실패")
    @Test
    public void createCommunityWithFileUploadFail_Fail() {
        // given
        List<MultipartFile> imageFiles = List.of(this.image, image);
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));

        // when & then
        doThrow(FileUploadException.class).when(fileService).storePostImages(imageFiles);

        assertThrows(FileUploadException.class,
                () -> postService.create(community.getId(), member1, createPostDto, imageFiles));

        // then
        assertThat(post.getImages().size()).isEqualTo(0);
        verify(postRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("게시글을 쓴 본인이 게시글 삭제시 삭제 성공")
    public void deleteByWriter_Success() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member1Membership));
        when(postRepository.findByIdWithAll(any())).thenReturn(Optional.ofNullable(post));
        Comment comment = Comment.builder().id(1L).content("hello").build();
        when(commentRepository.findByPostIdAndLevel(any(), any())).thenReturn(List.of(comment));

        // when
        postService.delete(community.getId(), post.getId(), member1);

        // then
        verify(imageRepository).deleteByPostId(any());
        verify(postRepository).deleteById(any());
    }

    @Test
    @DisplayName("Owner등급의 회원이 게시글 삭제시 삭제 성공")
    public void deleteByOwner_Success() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(ownerMembership));
        when(postRepository.findByIdWithAll(any())).thenReturn(Optional.ofNullable(post));

        Comment comment = Comment.builder().id(1L).content("hello").build();
        when(commentRepository.findByPostIdAndLevel(any(), any())).thenReturn(List.of(comment));

        // when
        postService.delete(community.getId(), post.getId(), owner);

        // then
        verify(imageRepository).deleteByPostId(any());
        verify(postRepository).deleteById(any());
    }

    @Test
    @DisplayName("가입하지 않은 회원이 게시글 삭제시 CommunityNotJoinedException 를 던지며 삭제 실패")
    public void deleteWithNotJoinedMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> postService.delete(any(), any(), member1));
    }

    @Test
    @DisplayName("삭제 권한이 없는 회원이 게시글 삭제시 AccessDeniedException 를 던지며 삭제 실패")
    public void deleteWithNotMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(member2Membership));
        when(postRepository.findByIdWithAll(any())).thenReturn(Optional.ofNullable(post));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> postService.delete(any(), any(), member2));
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    public void findOneByPostId_Success() {
        // given
        when(postRepository.findByIdAndCommunityId(any(), any())).thenReturn(Optional.ofNullable(post));
        post.setCreatedAt(LocalDateTime.now());

        // when
        PostDto postDto = postService.findById(any(), any(), member1);

        // then
        assertThat(postDto.getContent()).isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("해당 게시글이 없는 경우 PostNotFoundException를 던지며 게시글 단건 조회 실패")
    public void findOneByPostIdWithNotFoundPost_Fail() {
        // given
        when(postRepository.findByIdAndCommunityId(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class,
                () -> postService.findById(any(), any(), member1));
    }

    @Test
    @DisplayName("게시글 전체 조회 성공")
    public void findAllByCommunityId_Success() {
        // given
        when(postRepository.findAllByCommunityIdOrderByIdDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(post)));
        post.setCreatedAt(LocalDateTime.now());

        // when
        Page<PostDto> postDtos = postService.findAllByCommunityId(any(), member1, any());

        // then
        assertThat(postDtos.getSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원이 가입한 모든 커뮤니티의 게시글 조회")
    public void findAllByMember_Success() {
        // given
        when(communityMemberRepository.findByMember(member1)).thenReturn(List.of(member1Membership));
        when(postRepository.findByCommunityInOrderByIdDesc(any(), any())).thenReturn(new PageImpl<>(List.of(post)));
        post.setCreatedAt(LocalDateTime.now());

        // when
        Page<PostDto> postDtos = postService.findAllByMember(member1, any());

        // then
        assertThat(postDtos.getSize()).isEqualTo(1);
    }
}