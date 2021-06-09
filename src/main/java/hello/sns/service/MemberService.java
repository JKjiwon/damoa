package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.web.dto.member.JoinMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    MemberDto join(JoinMemberDto joinMemberDto);

    void checkDuplicatedEmail(String email);

    MemberDto updateProfileImage(Member currentMember, MultipartFile profileImage);

    MemberDto updateMember(Member currentMember, UpdateMemberDto updateMemberDto);

}
