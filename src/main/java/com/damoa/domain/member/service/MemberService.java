package com.damoa.domain.member.service;

import com.damoa.domain.member.entity.Member;
import com.damoa.domain.member.dto.CreateMemberDto;
import com.damoa.domain.member.dto.MemberDto;
import com.damoa.domain.member.dto.UpdateMemberDto;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    MemberDto create(CreateMemberDto dto);

    void checkDuplicatedEmail(String email);

    MemberDto updateProfileImage(Member currentMember, MultipartFile profileImage);

    MemberDto updateMember(Member currentMember, UpdateMemberDto dto);

}
