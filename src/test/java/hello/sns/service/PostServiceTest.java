package hello.sns.service;

import hello.sns.domain.category.Category;
import hello.sns.domain.community.Community;
import hello.sns.domain.community.MemberGrade;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Post;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.business.CommunityNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinedException;
import hello.sns.web.exception.business.FileUploadException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    CommunityRepository communityRepository;

    @Mock
    CommunityMemberRepository communityMemberRepository;

    @Mock
    FileService fileService;

    Member owner;
    Member member;
    Category category;
    Community community;
    CreatePostDto createPostDto;
    MockMultipartFile image;
    PostImageInfo postImageInfo;
    Post post;


    @BeforeEach
    void init() {
        owner = Member.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileMessage("오늘도 화이팅")
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        member = Member.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileMessage("오늘도 화이팅")
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
        community.join(member, MemberGrade.USER);

        createPostDto = new CreatePostDto("배영 기록");

        image = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        post = createPostDto.toEntity(member, community);

        postImageInfo = new PostImageInfo("hello", "2021/06/07/hello", 1);
    }

    @Test
    @DisplayName("이미지가 없고, 필수 입력값(title, content)가 주어졌을 경우 게시글 등록 성공")
    public void createPostWithoutImage_Success() {

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMemberAndCommunity(any(), any())).thenReturn(true);
        when(postRepository.save(any())).thenReturn(post);

        // when
        PostDto postDto = postService.create(community.getId(), member, createPostDto, null);

        // then
        verify(postRepository).save(any());
        Assertions.assertThat(postDto.getImages().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미지가 2개 있고, 필수 입력값(title, content)가 주어졌을 경우 게시글 등록 성공")
    public void createPostWithTwoImages_Success() {

        // given
        List<MultipartFile> imageFiles = List.of(image, image);
        List<PostImageInfo> postImageInfos = List.of(postImageInfo, postImageInfo);

        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMemberAndCommunity(any(), any())).thenReturn(true);
        when(postRepository.save(any())).thenReturn(post);
        when(fileService.uploadPostImages(imageFiles)).thenReturn(postImageInfos);

        // when
        PostDto postDto = postService.create(community.getId(), member, createPostDto, imageFiles);

        // then
        verify(fileService).uploadPostImages(any());
        verify(postRepository).save(any());
        Assertions.assertThat(postDto.getImages().size()).isEqualTo(2);
    }


    @Test
    @DisplayName("해당 커뮤니티가 존재하지 않으면 CommunityNotFoundException를 던지며 게시글 등록 실패")
    public void createPostWithNotFoundCommunity_Fail() {

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotFoundException.class,
                () -> postService.create(community.getId(), member, createPostDto, null));

        // then
        verify(postRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원이 아니면 CommunityNotJoinException를 던지며 게시글 등록 실패")
    public void createCommunityWithNotJoinedMember_Fail() {

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMemberAndCommunity(any(), any())).thenReturn(false);

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> postService.create(community.getId(), member, createPostDto, null));

        // then
        verify(postRepository, times(0)).save(any());
    }

    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 회원 프로필 이미지 업데이트 실패")
    @Test
    public void createCommunityWithFileUploadFail_Fail() {

        // given
        List<MultipartFile> imageFiles = List.of(this.image, image);
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMemberAndCommunity(any(), any())).thenReturn(true);
        when(postRepository.save(any())).thenReturn(post);

        // when & then
        doThrow(FileUploadException.class).when(fileService).uploadPostImages(imageFiles);

        assertThrows(FileUploadException.class,
                () -> postService.create(community.getId(), member, createPostDto, imageFiles));

        // then
        Assertions.assertThat(post.getImages().size()).isEqualTo(0);
    }
}