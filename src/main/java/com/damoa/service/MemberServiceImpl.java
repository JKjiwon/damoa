package com.damoa.service;

import com.damoa.domain.member.Member;
import com.damoa.repository.MemberRepository;
import com.damoa.web.dto.common.FileInfo;
import com.damoa.web.dto.member.CreateMemberDto;
import com.damoa.web.dto.member.MemberDto;
import com.damoa.web.dto.member.UpdateMemberDto;
import com.damoa.web.exception.business.EmailDuplicatedException;
import com.damoa.web.exception.business.MemberNotFoundException;
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
    @Override
    public MemberDto create(CreateMemberDto dto) {
        checkDuplicatedEmail(dto.getEmail());
        Member member = memberRepository.save(dto.toEntity());
        return new MemberDto(member);
    }

    @Override
    public void checkDuplicatedEmail(String email) {
        boolean isExistedEmail = memberRepository.existsByEmail(email);
        if (isExistedEmail) {
            throw new EmailDuplicatedException();
        }
    }

    @Transactional
    @Override
    public MemberDto updateProfileImage(Member currentMember,
                                        MultipartFile profileImage) {

        Member findMember = getMember(currentMember);
        FileInfo fileInfo = fileService.uploadImage(profileImage);
        fileService.deleteFile(findMember.getProfileImagePath());
        findMember.updateProfileImage(fileInfo);

        return new MemberDto(findMember);
    }

    @Transactional
    @Override
    public MemberDto updateMember(Member currentMember, UpdateMemberDto dto) {
        Member findMember = getMember(currentMember);
        findMember.update(dto);
        return new MemberDto(findMember);
    }

    private Member getMember(Member currentMember) {
        return memberRepository.findById(currentMember.getId()).orElseThrow(
                MemberNotFoundException::new);
    }

}
