package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.repository.MemberRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.member.JoinMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
import hello.sns.web.exception.business.EmailDuplicatedException;
import hello.sns.web.exception.business.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final FileService fileService;

    private final MemberRepository memberRepository;

    @Transactional
    public MemberDto join(JoinMemberDto joinMemberDto) {
        checkDuplicatedEmail(joinMemberDto.getEmail());
        Member member = joinMemberDto.toEntity();
        memberRepository.save(member);
        return new MemberDto(member);
    }

    public void checkDuplicatedEmail(String email) {
        boolean isExistedEmail = memberRepository.existsByEmail(email);
        if (isExistedEmail) {
            throw new EmailDuplicatedException();
        }
    }

    @Transactional
    public MemberDto updateProfileImage(Member currentMember,
                                        MultipartFile profileImage) {

        Member findMember = getMember(currentMember);
        FileInfo fileInfo = fileService.uploadImage(profileImage);
        fileService.deleteFile(findMember.getProfileImagePath());
        findMember.updateProfileImage(fileInfo);

        return new MemberDto(findMember);
    }

    @Transactional
    public MemberDto updateMember(Member currentMember, UpdateMemberDto updateMemberDto) {
        Member findMember = getMember(currentMember);
        findMember.update(updateMemberDto.getName(), updateMemberDto.getProfileMessage());
        return new MemberDto(findMember);
    }

    private Member getMember(Member currentMember) {
        return memberRepository.findById(currentMember.getId()).orElseThrow(
                MemberNotFoundException::new);
    }
}
