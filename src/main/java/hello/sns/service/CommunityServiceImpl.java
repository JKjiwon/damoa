package hello.sns.service;

import hello.sns.entity.category.Category;
import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.community.MemberGrade;
import hello.sns.entity.member.Member;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.CommunityAlreadyJoinException;
import hello.sns.web.exception.business.CommunityNameDuplicateException;
import hello.sns.web.exception.business.CommunityNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

        Category category = categoryService.getCategory(createCommunityDto.getCategory());
        Community community = Community.builder()
                .name(createCommunityDto.getName())
                .introduction(createCommunityDto.getName())
                .owner(currentMember)
                .category(category)
                .build();

        // community 영속화
        Community savedCommunity = communityRepository.save(community);

        // update - dirty checking
        if (mainImage != null) {
            FileInfo mainImageFile = fileService.uploadCommunityImageFile(mainImage, savedCommunity.getId());
            savedCommunity.changeMainImage(mainImageFile);
        }

        // update - dirty checking
        if (thumbNailImage != null) {
            FileInfo thumbNailImageFile = fileService.uploadCommunityImageFile(thumbNailImage, savedCommunity.getId());
            savedCommunity.changeThumbNailImage(thumbNailImageFile);
        }

        savedCommunity.joinCommunityMembers(currentMember, MemberGrade.OWNER);
        return new CommunityDto(savedCommunity);
    }

    @Override
    public void checkDuplicatedName(String name) {
        boolean isExistedName = communityRepository.existsByName(name);

        if (isExistedName) {
            throw new CommunityNameDuplicateException("이미 존재하는 커뮤니티 이름 입니다.");
        }
    }

    @Transactional
    @Override
    public void join(Member currentMember, Long communityId) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        // 이미 가입된 회원이면 CommunityAlreadyJoinException 던진다.
        Boolean isJoinedMember = communityMemberRepository.existsByMemberAndCommunity(currentMember, community);
        if (isJoinedMember) {
            throw new CommunityAlreadyJoinException("Already joined member");
        }

        // 커뮤니티 가입
        community.joinCommunityMembers(currentMember, MemberGrade.USER);
    }

    @Transactional
    @Override
    public void withdraw(Member currentMember, Long communityId) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        CommunityMember communityMember = communityMemberRepository.findByMemberAndCommunity(currentMember, community)
                .orElseThrow(() -> new CommunityNotJoinException("Not joined member"));

        // 가입된 회원 등급이 OWNER 라면 AccessDeniedException 던진다.
        if (communityMember.getMemberGrade().equals(MemberGrade.OWNER)) {
            throw new AccessDeniedException("Your grade is OWNER. Hand over the community to someone else");
        }

        // 커뮤니티 탈퇴
        community.withdrawCommunityMembers(communityMember);

        // currentMember 의 게시물, 사진 삭제 로직
    }

    @Transactional
    @Override
    public CommunityDto update(Long communityId,
                               Member currentMember,
                               UpdateCommunityDto updateCommunityDto,
                               MultipartFile mainImage, MultipartFile thumbNailImage) {

        // 커뮤니티 확인
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        // 가입된 회원인지 확인
        CommunityMember communityMember = communityMemberRepository.findByMemberAndCommunity(currentMember, community)
                .orElseThrow(() -> new CommunityNotJoinException("Not joined member"));

        // 가입된 회원의 등급이 OWNER 이거나 ADMIN 인지 확인
        MemberGrade memberGrade = communityMember.getMemberGrade();
        if (!(memberGrade.equals(MemberGrade.OWNER)) && !(memberGrade.equals(MemberGrade.ADMIN))) {
            throw new AccessDeniedException("Not ADMIN or OWNER");
        }
        // 커뮤니티 정보 수정
        Category category = categoryService.getCategory(updateCommunityDto.getCategory());
        community.update(updateCommunityDto.getIntroduction(), category);

        // 커뮤니티 사진 수정
        if (mainImage != null) {
            FileInfo mainImageFile = fileService.uploadCommunityImageFile(mainImage, community.getId());
            fileService.deleteFile(community.getMainImagePath());
            community.changeMainImage(mainImageFile);
        }

        if (thumbNailImage != null) {
            FileInfo thumbNailImageFile = fileService.uploadCommunityImageFile(thumbNailImage, community.getId());
            fileService.deleteFile(community.getThumbNailImagePath());
            community.changeThumbNailImage(thumbNailImageFile);
        }

        return new CommunityDto(community);
    }

    @Override
    public CommunityDto findById(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        return new CommunityDto(community);
    }

//    public List<CommunityDto> findByAll(Member currentMember, Pageable pageable) {
//        // 내가 가입한 커뮤니티는 안보이게?
//        // 내가 가입한 커뮤니티는 가입했다고 보이게??
//
//        // 내가 가입한 커뮤니티 조회
//        communityMemberRepository.findByMember(currentMember)
//        communityRepository.findAll(pageable);
//    }
}

