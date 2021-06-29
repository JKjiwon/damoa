package com.damoa.service;

import com.damoa.domain.member.Member;
import com.damoa.repository.MemberRepository;
import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.dto.member.CreateMemberDto;
import com.damoa.web.dto.member.MemberDto;
import com.damoa.web.dto.member.UpdateMemberDto;
import com.damoa.web.exception.business.EmailDuplicatedException;
import com.damoa.web.exception.business.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class MemberServiceTest {

    @InjectMocks
    MemberServiceImpl memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    FileService fileService;

    Member member;

    CreateMemberDto createMemberDto;

    UpdateMemberDto updateMemberDto;

    MockMultipartFile imageFile;

    UploadFile imageUploadFile;

    @BeforeEach
    public void init() {
        member = Member.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("user1234")
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        createMemberDto = CreateMemberDto.builder()
                .email("user2@email.com")
                .password("12341234")
                .name("Jiwon")
                .build();

        updateMemberDto = UpdateMemberDto.builder()
                .name("user3")
                .build();

        imageFile = new MockMultipartFile(
                "imageFile",
                "imageFile",
                "image/jpg",
                "imageFile".getBytes());

        imageUploadFile = new UploadFile("imageFile", "/Users/kimjiwon/studyProject/sns/uploads/1/imageFile");
    }

    @Test
    @DisplayName("필수 입력값(email, password, name)이 주어졌을 경우 회원 가입 성공")
    public void createMember_Success() {
        // given
        when(memberRepository.save(any())).thenReturn(member);

        // when
        MemberDto memberDto = memberService.create(createMemberDto);

        // then
        verify(memberRepository).save(any(Member.class));
        assertThat(memberDto.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("회원 생성시 중복된 이메일이 있을 경우 DuplicatedEmailException을 던지며 회원가입 실패")
    public void createMemberWithDuplicatedEmail_Fail() {
        // given
        String email = createMemberDto.getEmail();
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // when & then
        assertDoesNotThrow(
                () -> memberService.checkDuplicatedEmail(email)
        );

        // when
        verify(memberRepository).existsByEmail(email);
        verify(memberRepository,times(0)).save(any(Member.class));
    }

    @Test
    @DisplayName("필수 입력값(name)이 주어졌을 때 회원 정보 업데이트 성공")
    public void updateMemberTest_Success() {
        // given
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));

        // when
        MemberDto memberDto = memberService.updateMember(member, updateMemberDto);

        // then
        assertThat(memberDto.getName()).isEqualTo(updateMemberDto.getName());
    }

    @Test
    @DisplayName("회원 프로필 이미지가 주어졌을 때 회원 프로필 정보 업데이트 성공")
    public void updateProfileImage_Success() {
        // given
        String originalProfileImagePath = member.getProfileImagePath();
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));
        when(fileService.storeImage(imageFile)).thenReturn(imageUploadFile);

        // when
        memberService.updateProfileImage(member, imageFile);

        // then
        verify(fileService).deleteFile(originalProfileImagePath);
        verify(fileService).storeImage(imageFile);
        assertThat(member.getProfileImageName()).isEqualTo(imageUploadFile.getFileName());
        assertThat(member.getProfileImagePath()).isEqualTo(imageUploadFile.getFilePath());
    }

    @DisplayName("이미지 업로드 실패 시 FileUploadException을 던지며 회원 프로필 이미지 업데이트 실패")
    @Test
    public void updateProfileImageWithFileUploadFail_Fail() {
        // given
        MockMultipartFile invalidImage = new MockMultipartFile(
                "newImage",
                "newImage",
                "audio/mpeg",
                "newImage".getBytes());

        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));
        doThrow(FileUploadException.class).when(fileService).storeImage(invalidImage);

        // when & then
        assertThrows(FileUploadException.class,
                () -> memberService.updateProfileImage(member, invalidImage));

        // then
        verify(fileService, times(0)).deleteFile(member.getProfileImagePath());
    }

    @Test
    @DisplayName("이메일 중복 CommunityNameDuplicatedException 던진다. ")
    public void duplicateCommunityName_Fail() {
        // given
        when(memberRepository.existsByEmail(any())).thenReturn(true);

        // when & then
        assertThrows(EmailDuplicatedException.class,
                () -> memberService.checkDuplicatedEmail(any()));
    }
}