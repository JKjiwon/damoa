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
import java.util.Optional;
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
    public Long create(Member currentMember,
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
        return savedCommunity.getId();
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
        // currentMember 의 게시물, 사진 삭제 로직
    }

    @Transactional
    @Override
    public void update(Long communityId,
                               Member currentMember,
                               UpdateCommunityDto updateCommunityDto,
                               MultipartFile mainImage, MultipartFile thumbNailImage) {


        CommunityMember actor = getMembership(currentMember, communityId);
        if (!actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not ADMIN or OWNER");
        }

        Community community = actor.getCommunity();
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
    }

    @Override
    public CommunityDto findById(Long communityId, Member currentMember) {
        Community community = getCommunity(communityId);
        List<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember);
        List<Community> joinedCommunities = getJoinedCommunities(communityMembers);

        return new CommunityDto(community, joinedCommunities);
    }

    @Override
    public Page<CommunityDto> findByAll(Member currentMember, Pageable pageable) {
        Page<Community> communities = communityRepository.findAll(pageable);
        List<CommunityMember> communityMembers = communityMemberRepository.findByMember(currentMember);
        List<Community> joinedCommunities = getJoinedCommunities(communityMembers);

        return communities
                .map(community -> new CommunityDto(community, joinedCommunities));
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

