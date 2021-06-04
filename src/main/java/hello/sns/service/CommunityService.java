package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {
    CommunityDto create(CreateCommunityDto createCommunityDto, Member currentMember, MultipartFile mainImage, MultipartFile thumbNailImage);

    void checkDuplicatedName(String name);

    CommunityDto findById(Long communityId);

    void join(Member currentMember, Long communityId);

    void withdraw(Member currentMember, Long communityId);
}
