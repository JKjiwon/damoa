package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.repository.MemberRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.member.JoinMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
import hello.sns.web.exception.EmailDuplicateException;
import hello.sns.web.exception.FileUploadException;
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
    }

    @Test
    @DisplayName("필수 입력값(email, password, name)이 주어졌을 경우 회원 가입 성공")
    public void joinMemberTest() {

        // given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .email("user2@email.com")
                .password("12341234")
                .name("Jiwon")
                .build();

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

    @DisplayName("이메일 중복 체크 시, 중복된 이메일이 있을 경우 DuplicatedEmailException을 던진다.")
    @Test
    public void checkDuplicatedEmailTestWithDupe() {

        // given
        String email = "user@email.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThrows(
                EmailDuplicateException.class,
                () -> memberService.checkDuplicatedEmail(email)
        );

        verify(memberRepository).existsByEmail(email);
    }

    @DisplayName("이메일 중복 체크 시, 중복된 이메일이 있을 경우 DuplicatedEmailException을 던진다.")
    @Test
    public void checkDuplicatedEmailTestWithoutDupe() {

        // given
        String email = "user@email.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // when & then
        assertDoesNotThrow(
                () -> memberService.checkDuplicatedEmail(email)
        );

        verify(memberRepository).existsByEmail(email);
    }

    @DisplayName("필수 입력값(name)이 주어졌을 때 회원 정보 업데이트 성공")
    @Test
    public void updateMemberTest() {

        String updatedName = "user3";
        String updatedProfileMessage = "내일도 화이팅";
        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .name(updatedName)
                .profileMessage(updatedProfileMessage)
                .build();

        // given
        when(memberRepository.findById(savedMember.getId())).thenReturn(Optional.ofNullable(savedMember));

        // when
        MemberDto memberDto = memberService.updateMember(savedMember, updateMemberDto);

        // then
        assertThat(memberDto.getName()).isEqualTo(updatedName);
        assertThat(memberDto.getProfileMessage()).isEqualTo(updatedProfileMessage);
    }

    @DisplayName("회원 프로필 이미지가 주어졌을 때 회원 프로필 정보 업데이트 성공")
    @Test
    public void updateProfileImage() {
        // given
        MockMultipartFile newImage = new MockMultipartFile(
                "newImage",
                "newImage",
                "image/jpg",
                "newImage".getBytes());


        FileInfo fileInfo = new FileInfo("newImage", "/Users/kimjiwon/studyProject/sns/uploads/1/newImage");
        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(savedMember));
        when(fileService.uploadImageFile(newImage, savedMember.getId())).thenReturn(fileInfo);

        // when
        memberService.updateProfileImage(savedMember, newImage);

        // then
        verify(fileService).deleteFile(member.getProfileImagePath());
        verify(fileService).uploadImageFile(newImage, savedMember.getId());
        assertThat(savedMember.getProfileImageName()).isEqualTo(fileInfo.getFileName());
        assertThat(savedMember.getProfileImagePath()).isEqualTo(fileInfo.getFilePath());
    }

    @DisplayName("회원 프로필 이미지가 이미지 파일이 아닌 파일이 주어졌을 때 회원 프로필 정보 업데이트 실패")
    @Test
    public void updateProfileImageThatIsNotImageFile() {
        // given
        MockMultipartFile newImage = new MockMultipartFile(
                "newImage",
                "newImage",
                "audio/mpeg",
                "newImage".getBytes());

        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(savedMember));
        doThrow(FileUploadException.class).when(fileService).uploadImageFile(newImage, savedMember.getId());

        // when & then
        assertThrows(FileUploadException.class,
                () -> memberService.updateProfileImage(savedMember, newImage));

        verify(fileService, times(0)).deleteFile(savedMember.getProfileImagePath());
    }
}