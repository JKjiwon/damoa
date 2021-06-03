package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.web.dto.community.CreateCommunityDto;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {
    void create(CreateCommunityDto createCommunityDto, MultipartFile mainImage, MultipartFile thumbNailImage, Member currentMember);

    void checkDuplicatedName(String name);
}
