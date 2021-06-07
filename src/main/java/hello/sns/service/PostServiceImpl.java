package hello.sns.service;

import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Post;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.PostRepository;
import hello.sns.util.FileUtil;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.exception.business.CommunityNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CommunityRepository communityRepository;

    private final CommunityMemberRepository communityMemberRepository;

    private final FileService fileService;

    @Transactional
    @Override
    public void createPost(Long communityId, Member currentMember, CreatePostDto createPostDto, List<MultipartFile> postImageFiles) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        CommunityMember communityMember = communityMemberRepository.findByMemberAndCommunity(currentMember, community)
                .orElseThrow(() -> new CommunityNotJoinException("Not joined member"));

        // 포스트 생성
        Post post = createPostDto.toEntity(currentMember, community);
        postRepository.save(post);

        // 사진 업로드
        if (!postImageFiles.isEmpty()){
            if (postImageFiles.size() == 1) {
//                fileService.uploadPostImage()
            } else {

            }
        }
    }
}
