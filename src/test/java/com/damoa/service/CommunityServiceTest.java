package com.damoa.service;

import com.damoa.domain.community.Category;
import com.damoa.domain.community.Community;
import com.damoa.domain.community.CommunityMember;
import com.damoa.domain.community.MemberGrade;
import com.damoa.domain.member.Member;
import com.damoa.repository.CommunityMemberRepository;
import com.damoa.repository.CommunityRepository;
import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.dto.community.CommunityMemberDto;
import com.damoa.web.dto.community.CreateCommunityDto;
import com.damoa.web.dto.community.UpdateCommunityDto;
import com.damoa.web.exception.AccessDeniedException;
import com.damoa.web.exception.business.*;
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
    UploadFile imageUploadFile;

    @BeforeEach
    public void init() {
        owner = Member.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        member = Member.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.com")
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

        createCommunityDto = CreateCommunityDto.builder()
                .name("????????? ??????")
                .introduction("?????? ??????. ?????? ?????? ??? ??? ??????.")
                .category("??????")
                .build();

        updateCommunityDto = UpdateCommunityDto.builder()
                .category("??????")
                .introduction("?????? ????????? ?????????.!!")
                .build();

        imageFile = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());

        imageUploadFile = new UploadFile("newImage",
                "/Users/kimjiwon/studyProject/sns/uploads/communities/1/newImage");
    }

    @Test
    @DisplayName("???????????? ??????, ?????? ?????????(name, introduction, category)??? ???????????? ?????? ???????????? ?????? ??????")
    public void createCommunityTest_Success() {
        // given
        community.join(owner, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(categoryService.getCategory(any())).thenReturn(category);

        // when
        communityService.create(owner, createCommunityDto, null, null);

        // then
        verify(communityRepository).save(any(Community.class));
        verify(categoryService).getCategory(any(String.class));
        verify(fileService, times(0)).storeImage(any(MultipartFile.class));
    }

    @Test
    @DisplayName("?????????(Thumbnail, Main)??? 2??? ??????, ?????? ?????????(name, introduction, category)??? ???????????? ?????? ???????????? ?????? ??????")
    public void createCommunityWithImageTest_Success() {
        // given
        community.join(owner, MemberGrade.OWNER);

        when(communityRepository.existsByName(any())).thenReturn(false);
        when(communityRepository.save(any())).thenReturn(community);
        when(categoryService.getCategory(any())).thenReturn(category);
        when(fileService.storeImage(any(MultipartFile.class))).thenReturn(imageUploadFile);

        // when
        communityService.create(owner, createCommunityDto, imageFile, imageFile);

        // then
        verify(fileService, times(2)).storeImage(any(MultipartFile.class));
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ????????? ???????????? ?????? CommunityNameDuplicateException??? ????????? ???????????? ?????? ??????")
    public void createCommunityWithDuplicatedNameTest_Fail() {
        // given
        when(communityRepository.existsByName(any())).thenReturn(true);

        // when & then
        assertThrows(CommunityNameDuplicatedException.class,
                () -> communityService.create(owner, createCommunityDto, null, null));

        verify(communityRepository, times(0)).save(any(Community.class));
        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
    }

    @Test
    @DisplayName("????????? ????????? ?????? ??? FileUploadException??? ????????? ???????????? ?????? ??????")
    public void createCommunityWithNotImageFileTest_Fail() {
        // given
        when(communityRepository.existsByName(any())).thenReturn(false);
        doThrow(FileUploadException.class).when(fileService).storeImage(imageFile);

        // when & then
        assertThrows(FileUploadException.class,
                () -> communityService.create(owner, createCommunityDto, imageFile, imageFile));

        verify(communityMemberRepository, times(0)).save(any(CommunityMember.class));
        verify(communityRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("?????? ??????????????? ???????????? ?????? ???????????? ?????? ??????")
    public void joinCommunity_Success() {
        // given
        // ?????? communityMember ??? owner ??? ??????
        community.join(owner, MemberGrade.OWNER);

        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(false);

        // when
        communityService.join(member, community.getId());

        // then
        assertThat(community.getCommunityMembers().size()).isEqualTo(2); // owner, member ??? ??????
        verify(communityRepository).findById(community.getId());
        verify(communityMemberRepository).existsByMemberAndCommunityId(member, community.getId());
    }

    @Test
    @DisplayName("?????? ??????????????? ???????????? ????????? CommunityNotFoundException??? ????????? ?????? ??????")
    public void joinCommunityWithNotFoundCommunity_Fail() {
        // given
        when(communityRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotFoundException.class,
                () -> communityService.join(any(), community.getId()));

        // then
        verify(communityMemberRepository, times(0)).existsByMemberAndCommunityId(any(), any());
    }

    @Test
    @DisplayName("?????? ??????????????? ?????? ??????????????? CommunityAlreadyJoinException??? ????????? ?????? ??????")
    public void joinCommunityWithAlreadyJoinedMember_Fail() {
        // given
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));
        when(communityMemberRepository.existsByMemberAndCommunityId(any(), any())).thenReturn(true);

        // when & then
        assertThrows(CommunityAlreadyJoinedException.class,
                () -> communityService.join(any(), community.getId()));

        // then
        verify(communityMemberRepository).existsByMemberAndCommunityId(any(), any());
    }

    @Test
    @DisplayName("?????? ??????????????? ????????? ???????????????, MemberGrade??? OWNER??? ????????? ???????????? ?????? ??????")
    public void withdrawCommunity_Success() {
        // given
        // ?????? communityMember ??? owner, member ??? ??????
        community.join(owner, MemberGrade.OWNER);
        community.join(member, MemberGrade.USER);

        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(1))); // member

        // when
        communityService.withdraw(member, community.getId());

        // then
        assertThat(community.getCommunityMembers().size()).isEqualTo(1); // owner ??? ??????
        verify(communityMemberRepository).findByMemberAndCommunityId(member, community.getId());
    }

    @Test
    @DisplayName("?????? ??????????????? ????????? ????????? ????????? CommunityNotJoinException??? ????????? ???????????? ?????? ??????")
    public void withdrawCommunityWithNotJoinedMember_Fail(){
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> communityService.withdraw(member, community.getId()));

        // then
        verify(communityMemberRepository).findByMemberAndCommunityId(any(), any());
    }

    @Test
    @DisplayName("?????? ??????????????? ????????? ?????? ????????? OWNER?????? AccessDeniedException??? ????????? ???????????? ?????? ??????")
    public void withdrawCommunityWithOwnerMember_Fail(){
        // given
        community.join(owner, MemberGrade.OWNER);
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.of(community.getCommunityMembers().get(0)));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> communityService.withdraw(owner, community.getId()));

        // then
        assertThat(community.getCommunityMembers().size()).isEqualTo(1); // owner ??? ??????
        verify(communityMemberRepository).findByMemberAndCommunityId(owner, community.getId());
    }

    @Test
    @DisplayName("???????????? ??????, ?????? ?????????(introduction, category)??? ???????????? ?????? ???????????? ?????? ??????")
    public void updateCommunity_Success() {
        // given
        UpdateCommunityDto updateCommunityDto = UpdateCommunityDto.builder()
                .category("??????")
                .introduction("?????? ????????? ?????????.!!")
                .build();
        community.join(owner, MemberGrade.OWNER);

        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(0)));
        when(categoryService.getCategory(any()))
                .thenReturn(new Category(updateCommunityDto.getCategory()));

        // when
        communityService.update(community.getId(), owner, updateCommunityDto, null, null);

        // then
        assertThat(community.getIntroduction()).isEqualTo(updateCommunityDto.getIntroduction());
        assertThat(community.getCategory().getName()).isEqualTo(updateCommunityDto.getCategory());

        verify(fileService, times(0)).deleteFile(any());
        verify(fileService, times(0)).storeImage(any());
        verify(communityMemberRepository).findByMemberAndCommunityId(owner, community.getId());
        verify(categoryService).getCategory(updateCommunityDto.getCategory());
    }

    @Test
    @DisplayName("?????????(Thumbnail, Main)??? 2??? ??????, ?????? ?????????(introduction, category)??? ???????????? ?????? ???????????? ?????? ??????")
    public void updateCommunityWithImage_Success() {
        // given
        community.join(owner, MemberGrade.OWNER);

        when(fileService.storeImage(any())).thenReturn(imageUploadFile);
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(0)));
        when(categoryService.getCategory(any()))
                .thenReturn(new Category(updateCommunityDto.getCategory()));

        // when
        communityService.update(community.getId(), owner, updateCommunityDto, imageFile, imageFile);

        // then
        verify(fileService, times(2)).deleteFile(any());
        verify(fileService, times(2)).storeImage(any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? ??? FileUploadException??? ????????? ???????????? ????????? ???????????? ??????")
    public void updateCommunityWithFileUploadFail_Fail() {
        // given
        community.join(owner, MemberGrade.OWNER);

        when(communityMemberRepository.findByMemberAndCommunityId(any(), any()))
                .thenReturn(Optional.ofNullable(community.getCommunityMembers().get(0)));
        when(categoryService.getCategory(any()))
                .thenReturn(new Category(updateCommunityDto.getCategory()));

        doThrow(FileUploadException.class).when(fileService).storeImage(imageFile);

        // when & then
        assertThrows(FileUploadException.class,
                () -> communityService.update(community.getId(), owner, updateCommunityDto, imageFile, imageFile));

        // then
        verify(fileService, times(0)).deleteFile(any());
    }

    @Test
    @DisplayName("?????? ??????????????? ????????? ????????? ????????? CommunityNotJoinException ????????? ???????????? ???????????? ??????")
    public void updateCommunityWithNotJoinedMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.empty());

        // when
        assertThrows(CommunityNotJoinedException.class,
                () -> communityService.update(community.getId(), member, updateCommunityDto, imageFile, imageFile));
    }

    @Test
    @DisplayName("?????? ??????????????? ????????? ????????? ????????? OWNER ?????? ADMIN??? ?????? ?????? AccessDeniedException ????????? ???????????? ???????????? ??????")
    public void updateCommunityWithAccessDeniedMember_Fail() {
        // given
        community.join(owner, MemberGrade.OWNER);
        community.join(member, MemberGrade.USER);
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.ofNullable(community.getCommunityMembers().get(1)));

        // when
        assertThrows(AccessDeniedException.class,
                () -> communityService.update(community.getId(), member, updateCommunityDto, imageFile, imageFile));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ??? CommunityNameDuplicatedException ?????????. ")
    public void duplicateCommunityName_Fail() {
        // given
        when(communityRepository.existsByName(any())).thenReturn(true);

        // when & then
        assertThrows(CommunityNameDuplicatedException.class,
                () -> communityService.checkDuplicatedName(any()));
    }

    @Test
    @DisplayName("CommunityId??? ?????? ??????")
    public void findCommunityById_Success() {
        // given
        when(communityRepository.findById(any())).thenReturn(Optional.ofNullable(community));

        // when
        communityService.findById(any(), member);

        // then
        verify(communityRepository).findById(any());
        verify(communityMemberRepository).findByMember(member);
    }

    @Test
    @DisplayName("??????????????? ???????????? ????????? CommunityId??? ?????? ????????? CommunityNotFoundException ????????? ??????")
    public void findCommunityByIdWithNotFoundCommunity_Fail() {
        // given
        when(communityRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotFoundException.class,
                () -> communityService.findById(any(), member));

        // then
        verify(communityMemberRepository, times(0)).findByMember(member);
    }

    @Test
    @DisplayName("?????? ????????? Owner?????? ??????????????? ????????? ?????? ?????? ??????")
    public void findCommunityMemberByOwner_Success() {
        // given
        CommunityMember communityMember = new CommunityMember(community, owner, MemberGrade.OWNER);
        communityMember.setJoinedAt(LocalDateTime.now());

        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.of(communityMember));
        when(communityMemberRepository.findByCommunityId(any(), any())).thenReturn(new PageImpl<>(List.of(communityMember)));

        // when
        Page<CommunityMemberDto> communityMemberDtos = communityService.findCommunityMember(any(), owner, any());

        // then
        verify(communityMemberRepository).findByCommunityId(any(), any());
        assertThat(communityMemberDtos.getSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("?????? ????????? Admin?????? ??????????????? ????????? ?????? ?????? ??????")
    public void findCommunityMemberByAdmin_Success() {
        // given
        CommunityMember communityMember = new CommunityMember(community, member, MemberGrade.ADMIN);
        communityMember.setJoinedAt(LocalDateTime.now());

        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.of(communityMember));
        when(communityMemberRepository.findByCommunityId(any(), any())).thenReturn(new PageImpl<>(List.of(communityMember)));

        // when
        Page<CommunityMemberDto> communityMemberDtos = communityService.findCommunityMember(any(), member, any());

        // then
        verify(communityMemberRepository).findByCommunityId(any(), any());
        assertThat(communityMemberDtos.getSize()).isEqualTo(1);
    }


    @Test
    @DisplayName("?????? ????????? User?????? AccessDeniedException ??? ????????? ??????????????? ????????? ?????? ?????? ??????")
    public void findCommunityMemberByUser_Fail() {
        // given
        CommunityMember communityMember = new CommunityMember(community, member, MemberGrade.USER);
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.of(communityMember));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> communityService.findCommunityMember(any(), owner, any()));
    }

    @Test
    @DisplayName("??????????????? ???????????? ?????? ????????? ???????????? CommunityNotJoinedException ????????? ?????? ??????")
    public void findCommunityMemberWithNotJoinedMember_Fail() {
        // given
        when(communityMemberRepository.findByMemberAndCommunityId(any(), any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommunityNotJoinedException.class,
                () -> communityService.findCommunityMember(any(), owner, any()));
    }
}

