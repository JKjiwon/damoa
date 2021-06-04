package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {
    CommunityDto create(Member currentMember,
                        CreateCommunityDto createCommunityDto,
                        MultipartFile mainImage,
                        MultipartFile thumbNailImage);

    void checkDuplicatedName(String name);

    CommunityDto findById(Long communityId);

    void join(Member currentMember, Long communityId);

    void withdraw(Member currentMember, Long communityId);

    CommunityDto update(Long communityId,
                        Member currentMember,
                        UpdateCommunityDto updateCommunityDto,
                        MultipartFile mainImage, MultipartFile thumbNailImage);
}
