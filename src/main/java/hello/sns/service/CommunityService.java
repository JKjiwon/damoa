package hello.sns.service;

import hello.sns.repository.CommunityRepository;
import hello.sns.web.exception.CommunityNameDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final CommunityRepository communityRepository;

//    @Transactional
//    public CommunityResponseDto creatCommunity(CommunitySaveDto communitySaveDto) {
//        // 이름 중복 확인
//        validateDuplicateName(communitySaveDto.getName());
//
//        Community community = communitySaveDto.toEntity();
//
//
//        return new CommunityResponseDto(communityRepository.save(community));
//    }

//    @Transactional
//    public CommunityResponseDto updateCommunity(Long id, CommunityUpdateDto communityUpdateDto) {
//        Community community = communityRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티가 없습니다. id=" + id));
//
//        community.update(communityUpdateDto);
//        return new CommunityResponseDto(community);
//    }

    private void validateDuplicateName(String name) {
        if (communityRepository.existsByName(name)) {
            throw new CommunityNameDuplicateException("이미 존재하는 커뮤니티 이름 입니다.");
        }
    }
}
