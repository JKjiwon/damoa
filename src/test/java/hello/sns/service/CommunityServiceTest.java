package hello.sns.service;

import hello.sns.entity.category.Category;
import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.community.MemberGrade;
import hello.sns.entity.member.Member;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.exception.CommunityNameDuplicateException;
import hello.sns.web.exception.FileUploadException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
//@MockitoSettings(strictness = Strictness.LENIENT)
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

        community = Community.of("다모아 수영", "일단 와바. 수영 너도 할 수 있어.", member, category);
    }

    @Test
    @DisplayName("이미지가 없고, 필수 입력값(name, introduction, category)이 주어졌을 경우 커뮤니티 생성 성공")
    public void createCommunityTest() {

        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("community1")
                .introduction("Welcome to community1")
                .category("운동")
                .build();

        Category category = new Category("운동");
        Community community = Community.of(
                createCommunityDto.getName(),
                createCommunityDto.getIntroduction(),
                member, category);

        CommunityMember communityMember = CommunityMember.of(member, community, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(communityMemberRepository.save(any())).thenReturn(communityMember);
        when(categoryService.getCategory(any())).thenReturn(new Category("운동"));

        CommunityDto communityDto = communityService.create(createCommunityDto, member, null, null);

        verify(communityRepository).save(any());
        verify(communityMemberRepository).save(any());
        verify(categoryService).getCategory(any());
        verify(fileService, times(0)).uploadCommunityImageFile(any(MultipartFile.class), any(Long.class));

        Assertions.assertThat(createCommunityDto.getName()).isEqualTo(communityDto.getName());
    }

    @Test
    @DisplayName("이미지(Thumbnail, Main)가 2개 있고, 필수 입력값(name, introduction, category)이 주어졌을 경우 커뮤니티 생성 성공")
    public void createCommunityWithImageTest() {

        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("community1")
                .introduction("Welcome to community1")
                .category("운동")
                .build();

        Category category = new Category("운동");

        Community community = new Community(1L,
                createCommunityDto.getName(),
                createCommunityDto.getIntroduction(),
                member, null, null, null, null, category);


        MockMultipartFile imageFile = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        FileInfo imageFileInfo = new FileInfo("newImage",
                "/Users/kimjiwon/studyProject/sns/uploads/communities/1/newImage");

        CommunityMember communityMember = CommunityMember.of(member, community, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(communityMemberRepository.save(any())).thenReturn(communityMember);
        when(categoryService.getCategory(any())).thenReturn(new Category("운동"));
        when(fileService.uploadCommunityImageFile(any(MultipartFile.class), any(Long.class))).thenReturn(imageFileInfo);

        communityService.create(createCommunityDto, member, imageFile, imageFile);
        verify(fileService, times(2)).uploadCommunityImageFile(any(MultipartFile.class), any(Long.class));
    }

    @Test
    @DisplayName("이미 생성된 커뮤니티 이름이 주어졌을 경우 CommunityNameDuplicateException 을 던지며 커뮤니티 생성 실패")
    public void createCommunityWithDuplicatedNameTest() {

        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("다모아 수영")
                .introduction("Welcome to community1")
                .category("운동")
                .build();

        when(communityRepository.existsByName(any())).thenReturn(true);

        assertThrows(CommunityNameDuplicateException.class,
                () -> communityService.create(createCommunityDto, member, null, null));

        verify(communityRepository, times(0)).save(any(Community.class));
        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }

    @Test
    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 커뮤니티 생성 실패")
    public void createCommunityWithNotImageFileTest() {
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("community1")
                .introduction("Welcome to community1")
                .category("운동")
                .build();

        Category category = new Category("운동");

        Community community = new Community(1L,
                createCommunityDto.getName(),
                createCommunityDto.getIntroduction(),
                member, null, null, null, null, category);

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
        assertThrows(FileUploadException.class,
                () -> communityService.create(createCommunityDto, member, imageFile, imageFile));

        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }
}

