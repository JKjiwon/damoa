package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CommunityMemberDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {

    Long create(Member currentMember,
                        CreateCommunityDto createCommunityDto,
                        MultipartFile mainImage,
                        MultipartFile thumbNailImage);

    void checkDuplicatedName(String name);

    void join(Member currentMember, Long communityId);

    void withdraw(Member currentMember, Long communityId);

    void update(Long communityId,
                        Member currentMember,
                        UpdateCommunityDto updateCommunityDto,
                        MultipartFile mainImage, MultipartFile thumbNailImage);

    CommunityDto findById(Long communityId, Member currentMember);

    Page<CommunityMemberDto> findCommunityMember(Long communityId, Member currentMember, Pageable pageable);

    Page<CommunityDto> findAllSearch(Member currentMember, Pageable pageable, String keyword);
}
