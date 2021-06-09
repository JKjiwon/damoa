package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.repository.MemberRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.member.JoinMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    PasswordEncoder passwordEncoder;

    Member member;

    Member savedMember;

    JoinMemberDto joinMemberDto;

    UpdateMemberDto updateMemberDto;

    MockMultipartFile imageFile;

    FileInfo imageFileInfo;

    @BeforeEach
    public void init() {

        member = Member.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password("user1234")
                .profileMessage("오늘도 화이팅")
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        savedMember = Member.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .password(new BCryptPasswordEncoder().encode("user1234"))
                .profileMessage("오늘도 화이팅")
                .profileImageName("userImage")
                .profileImagePath("/Users/kimjiwon/studyProject/sns/uploads/1/userImage")
                .build();

        joinMemberDto = JoinMemberDto.builder()
                .email("user2@email.com")
                .password("12341234")
                .name("Jiwon")
                .build();

        updateMemberDto = UpdateMemberDto.builder()
                .name("user3")
                .profileMessage("내일도 화이팅")
                .build();

        imageFile = new MockMultipartFile(
                "imageFile",
                "imageFile",
                "image/jpg",
                "imageFile".getBytes());

        imageFileInfo = new FileInfo("imageFile", "/Users/kimjiwon/studyProject/sns/uploads/1/imageFile");

    }

    @Test
    @DisplayName("필수 입력값(email, password, name)이 주어졌을 경우 회원 가입 성공")
    public void joinMemberTest_Success() {

        // given
        Member member = joinMemberDto.toEntity();
        when(memberRepository.save(any())).thenReturn(member);
        when(passwordEncoder.encode(any())).thenReturn(any());

        // when
        MemberDto memberDto = memberService.join(joinMemberDto);

        // then
        verify(memberRepository).save(any(Member.class));
        assertThat(memberDto.getEmail()).isEqualTo(joinMemberDto.getEmail());
        assertThat(memberDto.getName()).isEqualTo(joinMemberDto.getName());
    }

    @DisplayName("회원 생성시 중복된 이메일이 있을 경우 DuplicatedEmailException을 던지며 회원가입 실패")
    @Test
    public void createMemberTestWithDuplicatedEmail_Fail() {

        // given
        String email = joinMemberDto.getEmail();
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // when & then
        assertDoesNotThrow(
                () -> memberService.checkDuplicatedEmail(email)
        );

        // when
        verify(memberRepository).existsByEmail(email);
        verify(memberRepository,times(0)).save(any(Member.class));
    }

    @DisplayName("필수 입력값(name)이 주어졌을 때 회원 정보 업데이트 성공")
    @Test
    public void updateMemberTest_Success() {

        // given
        when(memberRepository.findById(savedMember.getId())).thenReturn(Optional.ofNullable(savedMember));

        // when
        MemberDto memberDto = memberService.updateMember(savedMember, updateMemberDto);

        // then
        assertThat(memberDto.getName()).isEqualTo(updateMemberDto.getName());
        assertThat(memberDto.getProfileMessage()).isEqualTo(updateMemberDto.getProfileMessage());
    }

    @DisplayName("회원 프로필 이미지가 주어졌을 때 회원 프로필 정보 업데이트 성공")
    @Test
    public void updateProfileImage_Success() {

        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(savedMember));
        when(fileService.uploadImage(imageFile)).thenReturn(imageFileInfo);

        // when
        memberService.updateProfileImage(savedMember, imageFile);

        // then
        verify(fileService).deleteFile(member.getProfileImagePath());
        verify(fileService).uploadImage(imageFile);
        assertThat(savedMember.getProfileImageName()).isEqualTo(imageFileInfo.getFileName());
        assertThat(savedMember.getProfileImagePath()).isEqualTo(imageFileInfo.getFilePath());
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

        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(savedMember));
        doThrow(FileUploadException.class).when(fileService).uploadImage(invalidImage);

        // when & then
        assertThrows(FileUploadException.class,
                () -> memberService.updateProfileImage(savedMember, invalidImage));

        // then
        verify(fileService, times(0)).deleteFile(savedMember.getProfileImagePath());
    }
}