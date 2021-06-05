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
import hello.sns.web.dto.community.UpdateCommunityDto;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.*;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    Member owner;
    Member member;
    Category category;
    Community community;
    CreateCommunityDto createCommunityDto;
    UpdateCommunityDto updateCommunityDto;
    MockMultipartFile imageFile;
    FileInfo imageFileInfo;

    @BeforeEach
    public void init() {
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

        createCommunityDto = CreateCommunityDto.builder()
                .name("다모아 수영")
                .introduction("일단 와바. 수영 너도 할 수 있어.")
                .category("운동")
                .build();

        updateCommunityDto = UpdateCommunityDto.builder()
                .category("친목")
                .introduction("같이 떠들어 봅시다.!!")
                .build();

        imageFile = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        imageFileInfo = new FileInfo("newImage",
                "/Users/kimjiwon/studyProject/sns/uploads/communities/1/newImage");
    }

    @Test
    @DisplayName("이미지가 없고, 필수 입력값(name, introduction, category)이 주어졌을 경우 커뮤니티 생성 성공")
    public void createCommunityTest_Success() {

        // given
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(categoryService.getCategory(any())).thenReturn(category);

        // when
        communityService.create(owner, createCommunityDto, null, null);

        // then
        verify(communityRepository).save(any(Community.class));
        verify(categoryService).getCategory(any(String.class));
        verify(fileService, times(0)).uploadCommunityImageFile(any(MultipartFile.class), any(Long.class));
    }

    @Test
    @DisplayName("이미지(Thumbnail, Main)가 2개 있고, 필수 입력값(name, introduction, category)이 주어졌을 경우 커뮤니티 생성 성공")
    public void createCommunityWithImageTest_Success() {

        // given
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(categoryService.getCategory(any())).thenReturn(category);
        when(fileService.uploadCommunityImageFile(any(MultipartFile.class), any(Long.class))).thenReturn(imageFileInfo);

        // when
        communityService.create(owner, createCommunityDto, imageFile, imageFile);

        // then
        verify(fileService, times(2)).uploadCommunityImageFile(any(MultipartFile.class), any(Long.class));
    }

    @Test
    @DisplayName("이미 생성된 커뮤니티 이름이 주어졌을 경우 CommunityNameDuplicateException을 던지며 커뮤니티 생성 실패")
    public void createCommunityWithDuplicatedNameTest_Fail() {

        // given
        when(communityRepository.existsByName(any())).thenReturn(true);

        // when & then
        assertThrows(CommunityNameDuplicateException.class,
                () -> communityService.create(owner, createCommunityDto, null, null));

        verify(communityRepository, times(0)).save(any(Community.class));
        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }

    @Test
    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 커뮤니티 생성 실패")
    public void createCommunityWithNotImageFileTest_Fail() {

        // given
        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        doThrow(FileUploadException.class).when(fileService).uploadCommunityImageFile(imageFile, community.getId());

        // when & then
        assertThrows(FileUploadException.class,
                () -> communityService.create(owner, createCommunityDto, imageFile, imageFile));

        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입하지 않은 회원이면 가입 성공")
    public void joinCommunity_Success() {

        // given
        // 현재 communityMember 에 owner 이 포함
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMember(member)).thenReturn(false);

        // when
        communityService.join(member, community.getId());

        // then
        assertThat(community.getCommunityMembers().size()).isEqualTo(2); // owner, member 이 포함
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).existsByMember(member);
    }

    @Test
    @DisplayName("해당 커뮤니티가 존재하지 않으면 CommunityNotFoundException를 던지며 가입 실패")
    public void joinCommunityWithNotFoundCommunity_Fail() {

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotFoundException.class,
                () -> communityService.join(any(), community.getId()));

        // then
        verify(communityRepository, times(1)).findById(any());
        verify(communityMemberRepository, times(0)).existsByMember(any());
    }

    @Test
    @DisplayName("해당 커뮤니티에 이미 가입했으면 CommunityAlreadyJoinException를 던지며 가입 실패")
    public void joinCommunityWithAlreadyJoinedMember_Fail() {

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMember(any())).thenReturn(true);

        // when & then
        assertThrows(CommunityAlreadyJoinException.class,
                () -> communityService.join(any(), community.getId()));

        // then
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).existsByMember(any());
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원이면서, MemberGrade가 OWNER이 아니면 커뮤니티 탈퇴 성공")
    public void withdrawCommunity_Success() {

        // given
        // 현재 communityMember 에 owner, member 이 포함
        community.joinCommunityMembers(owner, MemberGrade.OWNER);
        community.joinCommunityMembers(member, MemberGrade.USER);

        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(member))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(1))); // member

        // when
        communityService.withdraw(member, community.getId());

        // then
        assertThat(community.getCommunityMembers().size()).isEqualTo(1); // owner 이 포함
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).findByMember(member);
    }

    @Test
    @DisplayName("해당 커뮤니티가 존재하지 않으면 CommunityNotFoundException를 던지며 커뮤니티 탈퇴 실패")
    public void withdrawCommunityWithNotFoundCommunity_Fail() {

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotFoundException.class,
                () -> communityService.withdraw(any(), community.getId()));

        // then
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository, times(0)).findByMember(any());
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원이 아니면 CommunityNotJoinException를 던지며 커뮤니티 탈퇴 실패")
    public void withdrawCommunityWithNotJoinedMember_Fail(){

        // given
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(member)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinException.class,
                () -> communityService.withdraw(member, community.getId()));

        // then
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).findByMember(any());
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원 등급이 OWNER라면 AccessDeniedException를 던지며 커뮤니티 탈퇴 실패")
    public void withdrawCommunityWithOwnerMember_Fail(){

        // given
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(owner)).thenReturn(Optional.of(community.getCommunityMembers().get(0)));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> communityService.withdraw(owner, community.getId()));

        // then
        assertThat(community.getCommunityMembers().size()).isEqualTo(1); // owner 이 포함
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).findByMember(owner);
    }

    @Test
    @DisplayName("이미지가 없고, 필수 입력값(introduction, category)이 주어졌을 경우 커뮤니티 수정 성공")
    public void updateCommunity_Success() {

        // given
        UpdateCommunityDto updateCommunityDto = UpdateCommunityDto.builder()
                .category("친목")
                .introduction("같이 떠들어 봅시다.!!")
                .build();
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(communityRepository.findById(community.getId())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(owner))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(0)));
        when(categoryService.getCategory(updateCommunityDto.getCategory()))
                .thenReturn(new Category(updateCommunityDto.getCategory()));

        // when
        communityService.update(community.getId(), owner, updateCommunityDto, null, null);

        // then
        assertThat(community.getIntroduction()).isEqualTo(updateCommunityDto.getIntroduction());
        assertThat(community.getCategory().getName()).isEqualTo(updateCommunityDto.getCategory());

        verify(fileService, times(0)).deleteFile(any());
        verify(fileService, times(0)).uploadCommunityImageFile(any(), any());
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).findByMember(owner);
        verify(categoryService).getCategory(updateCommunityDto.getCategory());
    }

    @Test
    @DisplayName("이미지(Thumbnail, Main)가 2개 있고, 필수 입력값(introduction, category)이 주어졌을 경우 커뮤니티 수정 성공")
    public void updateCommunityWithImage_Success() {
        // given
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(fileService.uploadCommunityImageFile(imageFile, community.getId())).thenReturn(imageFileInfo);
        when(communityRepository.findById(community.getId())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(owner))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(0)));
        when(categoryService.getCategory(updateCommunityDto.getCategory()))
                .thenReturn(new Category(updateCommunityDto.getCategory()));

        // when
        communityService.update(community.getId(), owner, updateCommunityDto, imageFile, imageFile);

        // then
        verify(fileService, times(2)).deleteFile(any());
        verify(fileService, times(2)).uploadCommunityImageFile(any(), any());
    }

    @Test
    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 커뮤니티 이미지 업데이트 실패")
    public void updateCommunityWithFileUploadFail_Fail() {

        // given
        community.joinCommunityMembers(owner, MemberGrade.OWNER);

        when(communityRepository.findById(community.getId())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(owner))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(0)));
        when(categoryService.getCategory(updateCommunityDto.getCategory()))
                .thenReturn(new Category(updateCommunityDto.getCategory()));

        doThrow(FileUploadException.class).when(fileService).uploadCommunityImageFile(imageFile, community.getId());

        // when & then
        assertThrows(FileUploadException.class,
                () -> communityService.update(community.getId(), owner, updateCommunityDto, imageFile, imageFile));

        // then
        verify(fileService, times(0)).deleteFile(any());
    }

    @Test
    @DisplayName("해당 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던지며 커뮤니티 업데이트 실패")
    public void updateCommunityWithNotFoundCommunity_Fail() {
        // given
        when(communityRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotFoundException.class,
                () -> communityService.update(community.getId(), owner, updateCommunityDto, imageFile, imageFile));
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원이 아니면 CommunityNotJoinException 던지며 커뮤니티 업데이트 실패")
    public void updateCommunityWithNotJoinedMember_Fail() {

        // given
        when(communityRepository.findById(community.getId())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(member)).thenReturn(Optional.empty());

        // when
        assertThrows(CommunityNotJoinException.class,
                () -> communityService.update(community.getId(), member, updateCommunityDto, imageFile, imageFile));
    }

    @Test
    @DisplayName("해당 커뮤니티에 가입된 회원의 등급이 OWNER 또는 ADMIN이 아닌 경우 AccessDeniedException 던지며 커뮤니티 업데이트 실패")
    public void updateCommunityWithAccessDeniedMember_Fail() {

        // given
        community.joinCommunityMembers(owner, MemberGrade.OWNER);
        community.joinCommunityMembers(member, MemberGrade.USER);
        when(communityRepository.findById(community.getId())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.findByMember(member)).thenReturn(Optional.ofNullable(community.getCommunityMembers().get(1)));

        // when
        assertThrows(AccessDeniedException.class,
                () -> communityService.update(community.getId(), member, updateCommunityDto, imageFile, imageFile));
    }
}

