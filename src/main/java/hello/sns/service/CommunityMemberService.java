package hello.sns.service;

import hello.sns.entity.community.CommunityMember;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.web.exception.business.CommunityNotJoinedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommunityMemberService {

    private final CommunityMemberRepository communityMemberRepository;

    public CommunityMember getMemberShip(Long memberId, Long communityId) {
        CommunityMember communityMember = communityMemberRepository.findByMemberIdAndCommunityId(memberId, communityId)
                .orElseThrow(CommunityNotJoinedException::new);
        return communityMember;
    }

    public void validateMembership(Long memberId, Long communityId) {
        Boolean isJoinedMember = communityMemberRepository.existsByMemberIdAndCommunityId(memberId, communityId);
        if (!isJoinedMember) {
            throw new CommunityNotJoinedException("Not joined member");
        }
    }
}
