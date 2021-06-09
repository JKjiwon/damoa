package hello.sns.service;

import hello.sns.domain.category.Category;
import hello.sns.domain.community.Community;
import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.community.MemberGrade;
import hello.sns.domain.member.Member;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.CommunityAlreadyJoinedException;
import hello.sns.web.exception.business.CommunityNameDuplicatedException;
import hello.sns.web.exception.business.CommunityNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinedException;
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
                               CreateCommunityDto createCommunityDto,
                               MultipartFile mainImage, MultipartFile thumbNailImage) {

        checkDuplicatedName(createCommunityDto.getName());

        Category category = categoryService.addCategory(createCommunityDto.getCategory());
        Community community = createCommunityDto.toEntity(currentMember, category);
        Community savedCommunity = communityRepository.save(community);

        if (mainImage != null) {
            FileInfo mainImageFile = fileService.uploadImage(mainImage);
            savedCommunity.changeMainImage(mainImageFile);
        }
        if (thumbNailImage != null) {
            FileInfo thumbNailImageFile = fileService.uploadImage(thumbNailImage);
            savedCommunity.changeThumbNailImage(thumbNailImageFile);
        }
        savedCommunity.join(currentMember, MemberGrade.OWNER);
        return new CommunityDto(savedCommunity, true);
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
        validateMembership(currentMember, community);
        community.join(currentMember, MemberGrade.USER);
    }

    @Transactional
    @Override
    public void withdraw(Member currentMember, Long communityId) {
        Community community = getCommunity(communityId);
        CommunityMember communityMember = getMembership(currentMember, community);
        if (communityMember.isOwner()) {
            throw new AccessDeniedException("Your grade is OWNER. Hand over the community to someone else");
        }

        community.withdraw(communityMember);

        // currentMember 의 게시물, 사진 삭제 로직
    }

    @Transactional
    @Override
    public CommunityDto update(Long communityId,
                               Member currentMember,
                               UpdateCommunityDto updateCommunityDto,
                               MultipartFile mainImage, MultipartFile thumbNailImage) {

        Community community = getCommunity(communityId);
        CommunityMember communityMember = getMembership(currentMember, community);
        if (!communityMember.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not ADMIN or OWNER");
        }

        Category category = categoryService.addCategory(updateCommunityDto.getCategory());
        community.update(updateCommunityDto.getIntroduction(), category);

        if (mainImage != null) {
            FileInfo mainImageFile = fileService.uploadImage(mainImage);
            fileService.deleteFile(community.getMainImagePath());
            community.changeMainImage(mainImageFile);
        }
        if (thumbNailImage != null) {
            FileInfo thumbNailImageFile = fileService.uploadImage(thumbNailImage);
            fileService.deleteFile(community.getThumbNailImagePath());
            community.changeThumbNailImage(thumbNailImageFile);
        }

        return new CommunityDto(community, true);
    }

    @Override
    public CommunityDto findById(Long communityId, Member currentMember) {
        Community community = getCommunity(communityId);
        List<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember);
        List<Community> joinedCommunities = getJoinedCommunities(communityMembers);

        return new CommunityDto(community, joinedCommunities.contains(community));
    }

    @Override
    public Page<CommunityDto> findByAll(Member currentMember, Pageable pageable) {
        Page<Community> communities = communityRepository.findAll(pageable);
        List<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember);
        List<Community> joinedCommunities = getJoinedCommunities(communityMembers);

        return communities
                .map(community -> new CommunityDto(community, joinedCommunities.contains(community)));
    }

    private void validateMembership(Member currentMember, Community community) {
        Boolean isJoinedMember = communityMemberRepository.existsByMemberAndCommunity(currentMember, community);
        if (isJoinedMember) {
            throw new CommunityAlreadyJoinedException();
        }
    }

    private Community getCommunity(Long communityId) {
        return communityRepository.findById(communityId).orElseThrow(
                CommunityNotFoundException::new);
    }

    private CommunityMember getMembership(Member currentMember, Community community) {
        return communityMemberRepository.findByMemberAndCommunity(currentMember, community)
                .orElseThrow(CommunityNotJoinedException::new);
    }

    private List<Community> getJoinedCommunities(List<CommunityMember> communityMembers) {
        return communityMembers.stream()
                .map(CommunityMember::getCommunity)
                .collect(Collectors.toList());
    }
}

