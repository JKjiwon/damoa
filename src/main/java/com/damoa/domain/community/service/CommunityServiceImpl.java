package com.damoa.domain.community.service;

import com.damoa.domain.community.dto.*;
import com.damoa.domain.community.entity.Category;
import com.damoa.domain.community.entity.Community;
import com.damoa.domain.community.entity.CommunityMember;
import com.damoa.domain.community.entity.MemberGrade;
import com.damoa.domain.member.entity.Member;
import com.damoa.domain.community.repository.CommunityMemberRepository;
import com.damoa.domain.community.repository.CommunityRepository;
import com.damoa.domain.common.service.FileService;
import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.exception.AccessDeniedException;
import com.damoa.web.exception.business.CommunityAlreadyJoinedException;
import com.damoa.web.exception.business.CommunityNameDuplicatedException;
import com.damoa.web.exception.business.CommunityNotFoundException;
import com.damoa.web.exception.business.CommunityNotJoinedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    private final FileService fileService;

    private final CommunityMemberRepository communityMemberRepository;

    private final CategoryService categoryService;

    @Transactional
    @Override
    public CommunityDto create(Member currentMember,
                               CreateCommunityDto dto,
                               MultipartFile mainImage, MultipartFile thumbNailImage) {

        checkDuplicatedName(dto.getName());
        Category category = categoryService.getCategory(dto.getCategory());
        Community community = dto.toEntity(currentMember, category);

        if (mainImage != null) {
            UploadFile mainImageFile = fileService.storeImage(mainImage);
            community.changeMainImage(mainImageFile);
        }

        if (thumbNailImage != null) {
            UploadFile thumbNailImageFile = fileService.storeImage(thumbNailImage);
            community.changeThumbNailImage(thumbNailImageFile);
        }

        return new CommunityDto(communityRepository.save(community));
    }

    @Override
    public void checkDuplicatedName(String name) {
        boolean isExistedName = communityRepository.existsByName(name);

        if (isExistedName) {
            throw new CommunityNameDuplicatedException();
        }
    }

    @Transactional
    @Override
    public void join(Member currentMember, Long communityId) {
        Community community = getCommunity(communityId);
        checkJoinedMember(currentMember, communityId);
        community.join(currentMember, MemberGrade.USER);
    }

    @Transactional
    @Override
    public void withdraw(Member currentMember, Long communityId) {
        CommunityMember actor = getMembership(currentMember, communityId);
        if (actor.isOwner()) {
            throw new AccessDeniedException("Your grade is OWNER. Hand over the community to someone else");
        }

        Community community = actor.getCommunity();
        community.withdraw(actor);
    }

    @Transactional
    @Override
    public CommunityDto update(Long communityId,
                               Member currentMember,
                               UpdateCommunityDto dto,
                               MultipartFile mainImage, MultipartFile thumbNailImage) {

        CommunityMember actor = getMembership(currentMember, communityId);
        if (!actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not ADMIN or OWNER");
        }

        Community community = actor.getCommunity();
        Category category = categoryService.getCategory(dto.getCategory());
        community.update(dto.getIntroduction(), category);

        if (mainImage != null) {
            UploadFile mainImageFile = fileService.storeImage(mainImage);
            fileService.deleteFile(community.getMainImagePath());
            community.changeMainImage(mainImageFile);
        }

        if (thumbNailImage != null) {
            UploadFile thumbNailImageFile = fileService.storeImage(thumbNailImage);
            fileService.deleteFile(community.getThumbNailImagePath());
            community.changeThumbNailImage(thumbNailImageFile);
        }
        return new CommunityDto(community);
    }

    @Override
    public CommunityDto findById(Long communityId, Member currentMember) {
        Community community = getCommunity(communityId);
        List<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember);
        List<Community> joinedCommunities = getJoinedCommunities(communityMembers);
        return new CommunityDto(community, joinedCommunities);
    }

    @Override
    public Page<CommunityMemberDto> findCommunityMember(Long communityId, Member currentMember, Pageable pageable) {
        CommunityMember actor = getMembership(currentMember, communityId);
        if (!actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not ADMIN or OWNER");
        }
        Page<CommunityMember> communityMembers = communityMemberRepository.findByCommunityId(communityId, pageable);

        return communityMembers.map(CommunityMemberDto::new);
    }

    @Override
    public Page<CommunityDto> findAllSearch(Member currentMember, Pageable pageable, String keyword) {
        Page<Community> communities = communityRepository.findAllSearch("nci", keyword, pageable);
        List<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember);
        List<Community> joinedCommunities = getJoinedCommunities(communityMembers);

        return communities
                .map(community -> new CommunityDto(community, joinedCommunities));
    }

    @Override
    public Page<JoinedCommunityDto> findByCurrentMember(Member currentMember, Pageable pageable) {
        Page<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember, pageable);
        return communityMembers.map(JoinedCommunityDto::new);
    }

    private Community getCommunity(Long communityId) {
        return communityRepository.findById(communityId).orElseThrow(
                CommunityNotFoundException::new);
    }

    private CommunityMember getMembership(Member currentMember, Long communityId) {
        return communityMemberRepository.findByMemberAndCommunityId(currentMember, communityId)
                .orElseThrow(CommunityNotJoinedException::new);
    }

    private List<Community> getJoinedCommunities(List<CommunityMember> communityMembers) {
        return communityMembers.stream()
                .map(CommunityMember::getCommunity)
                .collect(Collectors.toList());
    }

    private void checkJoinedMember(Member member, Long communityId) {
        boolean isJoinedMember = communityMemberRepository
                .existsByMemberAndCommunityId(member, communityId);
        if (isJoinedMember) {
            throw new CommunityAlreadyJoinedException();
        }
    }
}

