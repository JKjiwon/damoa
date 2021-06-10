package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.web.dto.member.CreateMemberDto;
import hello.sns.web.dto.member.MemberDto;
import hello.sns.web.dto.member.UpdateMemberDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    Long join(CreateMemberDto createMemberDto);

    void checkDuplicatedEmail(String email);

    MemberDto updateProfileImage(Member currentMember, MultipartFile profileImage);

    MemberDto updateMember(Member currentMember, UpdateMemberDto updateMemberDto);

}
