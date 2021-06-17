package com.damoa.service;

import com.damoa.domain.member.Member;
import com.damoa.web.dto.community.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {

    CommunityDto create(Member currentMember,
                        CreateCommunityDto createCommunityDto,
                        MultipartFile mainImage,
                        MultipartFile thumbNailImage);

    void checkDuplicatedName(String name);

    void join(Member currentMember, Long communityId);

    void withdraw(Member currentMember, Long communityId);

    CommunityDto update(Long communityId,
                        Member currentMember,
                        UpdateCommunityDto updateCommunityDto,
                        MultipartFile mainImage, MultipartFile thumbNailImage);

    CommunityDto findById(Long communityId, Member currentMember);

    Page<CommunityMemberDto> findCommunityMember(Long communityId, Member currentMember, Pageable pageable);

    Page<CommunityDto> findAllSearch(Member currentMember, Pageable pageable, String keyword);

    Page<JoinedCommunityDto> findByCurrentMember(Member currentMember, Pageable pageable);
}
