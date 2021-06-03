package hello.sns.service;

import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import hello.sns.repository.CommunityRepository;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.exception.CommunityNameDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService{

    private final CommunityRepository communityRepository;
    private final FileService fileService;

    @Transactional
    @Override
    public void create(CreateCommunityDto createCommunityDto, MultipartFile mainImage, MultipartFile thumbNailImage, Member currentMember) {
        checkDuplicatedName(createCommunityDto.getName());
        Community community = createCommunityDto.toEntity();
        community.changeOwner(currentMember);

        if (mainImage != null) {
            FileInfo mainImageFile = fileService.uploadCommunityImageFile(mainImage, createCommunityDto.getName());
            community.changeMainImage(mainImageFile);
        }

        if (thumbNailImage != null) {
            FileInfo thumbNailImageFile = fileService.uploadCommunityImageFile(thumbNailImage, createCommunityDto.getName());
            community.changeThumbNailImage(thumbNailImageFile);
        }
        communityRepository.save(community);
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
