package com.damoa.service;

import com.damoa.domain.member.Member;
import com.damoa.web.dto.member.CreateMemberDto;
import com.damoa.web.dto.member.MemberDto;
import com.damoa.web.dto.member.UpdateMemberDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    MemberDto create(CreateMemberDto createMemberDto);

    void checkDuplicatedEmail(String email);

    MemberDto updateProfileImage(Member currentMember, MultipartFile profileImage);

    MemberDto updateMember(Member currentMember, UpdateMemberDto updateMemberDto);

}
