package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.repository.MemberRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.member.JoinMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
import hello.sns.web.exception.EmailDuplicateException;
import hello.sns.web.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDto join(JoinMemberDto joinMemberDto) {
        checkDuplicatedEmail(joinMemberDto.getEmail());
        joinMemberDto.setPassword(passwordEncoder.encode(joinMemberDto.getPassword()));
        Member member = joinMemberDto.toEntity();
        memberRepository.save(member);
        return new MemberDto(member);
    }

    @Transactional(readOnly = true)
    public void checkDuplicatedEmail(String email) {
        boolean isExistedEmail = memberRepository.existsByEmail(email);

        if (isExistedEmail) {
            throw new EmailDuplicateException("이미 존재하는 이메일 입니다.");
        }
    }

    public MemberDto updateProfileImage(Member currentMember,
                                        MultipartFile profileImage){

        Member findMember = memberRepository.findById(currentMember.getId()).orElseThrow(
                () -> new MemberNotFoundException("해당 회원이 존재하지 않습니다."));

        FileInfo fileInfo = fileService.uploadMemberImageFile(profileImage, findMember.getId());
        fileService.deleteFile(findMember.getProfileImagePath());
        findMember.updateProfileImage(fileInfo);

        return new MemberDto(findMember);
    }

    public MemberDto updateMember(Member currentMember, UpdateMemberDto updateMemberDto) {

        Member findMember = memberRepository.findById(currentMember.getId()).orElseThrow(
                () -> new MemberNotFoundException("해당 회원이 존재하지 않습니다."));

        findMember.update(updateMemberDto.getName(), updateMemberDto.getProfileMessage());

        return new MemberDto(findMember);
    }
}
