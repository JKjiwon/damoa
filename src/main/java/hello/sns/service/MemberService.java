package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.repository.MemberRepository;
import hello.sns.web.dto.common.FileDto;
import hello.sns.web.dto.member.JoinMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
import hello.sns.web.exception.EmailDuplicatedException;
import hello.sns.web.exception.FileUploadException;
import hello.sns.web.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDto join(JoinMemberDto joinMemberDto) {
        validateDuplicateEmail(joinMemberDto.getEmail());

        Member member = new Member(joinMemberDto.getEmail(),
                passwordEncoder.encode(joinMemberDto.getPassword()),
                joinMemberDto.getName());

        memberRepository.save(member);

        return new MemberDto(member);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new EmailDuplicatedException("이미 존재하는 이메일 입니다.");
        }
    }

    public MemberDto updateProfileImage(Member currentMember,
                                        MultipartFile profileImage) throws FileUploadException {

        Member findMember = memberRepository.findById(currentMember.getId()).orElseThrow(
                () -> new MemberNotFoundException("해당 회원이 존재하지 않습니다."));


        FileDto fileDto = fileService.uploadFile(profileImage, findMember.getId());
        String profileImageUrl = currentMember.getProfileImagePath();

        if (currentMember.getProfileImagePath() != null) {
            fileService.deleteFile(profileImageUrl);
        }

        findMember.updateProfileImage(fileDto.getFileName(), fileDto.getFilePath());

        return new MemberDto(findMember);
    }

    public MemberDto updateMember(Member currentMember, UpdateMemberDto updateMemberDto) {

        Member findMember = memberRepository.findById(currentMember.getId()).orElseThrow(
                () -> new MemberNotFoundException("해당 회원이 존재하지 않습니다."));

        findMember.update(updateMemberDto.getName(), updateMemberDto.getProfileMessage());

        return new MemberDto(findMember);
    }
}
