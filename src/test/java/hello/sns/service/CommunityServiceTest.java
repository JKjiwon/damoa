package hello.sns.service;

import hello.sns.entity.category.Category;
import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.community.MemberGrade;
import hello.sns.entity.member.Member;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.exception.business.CommunityNameDuplicateException;
import hello.sns.web.exception.business.FileUploadException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class CommunityServiceTest {
    @InjectMocks
    CommunityServiceImpl communityService;

    @Mock
    CommunityRepository communityRepository;

    @Mock
    CommunityMemberRepository communityMemberRepository;

    @Mock
    FileService fileService;

    @Mock
    CategoryService categoryService;

    Member member;
    Community community;
    Category category;

    @BeforeEach
    public void init() {
        member = Member.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
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
                .owner(member)
                .category(category)
                .build();
    }

    @Test
    @DisplayName("이미지가 없고, 필수 입력값(name, introduction, category)이 주어졌을 경우 커뮤니티 생성 성공")
    public void createCommunityTest() {
        // given
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("다모아 수영")
                .introduction("일단 와바. 수영 너도 할 수 있어.")
                .category("운동")
                .build();

        community.joinCommunityMembers(member, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(categoryService.getCategory(any())).thenReturn(category);

        // when
        communityService.create(member, createCommunityDto, null, null);

        // then
        verify(communityRepository).save(any(Community.class));
        verify(categoryService).getCategory(any(String.class));
        verify(fileService, times(0)).uploadCommunityImageFile(any(MultipartFile.class), any(Long.class));
    }

    @Test
    @DisplayName("이미지(Thumbnail, Main)가 2개 있고, 필수 입력값(name, introduction, category)이 주어졌을 경우 커뮤니티 생성 성공")
    public void createCommunityWithImageTest() {
        // given
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("다모아 수영")
                .introduction("일단 와바. 수영 너도 할 수 있어.")
                .category("운동")
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        FileInfo imageFileInfo = new FileInfo("newImage",
                "/Users/kimjiwon/studyProject/sns/uploads/communities/1/newImage");

        community.joinCommunityMembers(member, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(categoryService.getCategory(any())).thenReturn(category);
        when(fileService.uploadCommunityImageFile(any(MultipartFile.class), any(Long.class))).thenReturn(imageFileInfo);

        // when
        communityService.create(member, createCommunityDto, imageFile, imageFile);

        // then
        verify(fileService, times(2)).uploadCommunityImageFile(any(MultipartFile.class), any(Long.class));
    }

    @Test
    @DisplayName("이미 생성된 커뮤니티 이름이 주어졌을 경우 CommunityNameDuplicateException 을 던지며 커뮤니티 생성 실패")
    public void createCommunityWithDuplicatedNameTest() {
        // given
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("다모아 수영")
                .introduction("일단 와바. 수영 너도 할 수 있어.")
                .category("운동")
                .build();

        when(communityRepository.existsByName(any())).thenReturn(true);

        // when & then
        assertThrows(CommunityNameDuplicateException.class,
                () -> communityService.create(member, createCommunityDto, null, null));

        verify(communityRepository, times(0)).save(any(Community.class));
        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }

    @Test
    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 커뮤니티 생성 실패")
    public void createCommunityWithNotImageFileTest() {
        // given
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("다모아 수영")
                .introduction("일단 와바. 수영 너도 할 수 있어.")
                .category("운동")
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        FileInfo imageFileInfo = new FileInfo("newImage",
                "/Users/kimjiwon/studyProject/sns/uploads/communities/1/newImage");

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        doThrow(FileUploadException.class).when(fileService).uploadCommunityImageFile(imageFile, community.getId());

        // when & then
        assertThrows(FileUploadException.class,
                () -> communityService.create(member, createCommunityDto, imageFile, imageFile));

        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }
}

