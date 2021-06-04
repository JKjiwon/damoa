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
import hello.sns.web.exception.CommunityNameDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService{

    private final CommunityRepository communityRepository;
    private final FileService fileService;
    private final CommunityMemberRepository communityMemberRepository;
    private final CategoryService categoryService;

    @Transactional
    @Override
    public CommunityDto create(CreateCommunityDto createCommunityDto, Member currentMember,
                                 MultipartFile mainImage, MultipartFile thumbNailImage) {
        checkDuplicatedName(createCommunityDto.getName());

        Category category = categoryService.getCategory(createCommunityDto.getCategory());
        Community community = Community.of(createCommunityDto.getName(),
                createCommunityDto.getIntroduction(), currentMember, category);

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

        CommunityMember communityMember = CommunityMember.of(currentMember, savedCommunity, MemberGrade.OWNER);
        communityMemberRepository.save(communityMember);

        return new CommunityDto(savedCommunity);
    }


    @Override
    public void checkDuplicatedName(String name) {
        boolean isExistedName = communityRepository.existsByName(name);

        if (isExistedName) {
            throw new CommunityNameDuplicateException("이미 존재하는 커뮤니티 이름 입니다.");
        }
    }

//    @Transactional
//    public CommunityResponseDto updateCommunity(Long id, CommunityUpdateDto communityUpdateDto) {
//        Community community = communityRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티가 없습니다. id=" + id));
//
//        community.update(communityUpdateDto);
//        return new CommunityResponseDto(community);
//    }
}
