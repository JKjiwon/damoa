package hello.sns.service;

import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Post;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.business.CommunityNotFoundException;
import hello.sns.web.exception.business.CommunityNotJoinException;
import hello.sns.web.exception.business.PostNotFoundException;
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
    public PostDto create(Long communityId, Member currentMember,
                          CreatePostDto createPostDto, List<MultipartFile> postImageFiles) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = getCommunity(communityId);

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        validateMembership(currentMember, community);

        // 게시글 생성
        Post post = createPostDto.toEntity(currentMember, community);
        Post savedPost = postRepository.save(post);

        // 사진 업로드 - 게시글과 사진은 생명주가기 같다. -> Cascade.All 로 설정
        if (postImageFiles != null && !postImageFiles.isEmpty()) {
            List<PostImageInfo> postImageInfos = fileService.uploadPostImages(postImageFiles);
            postImageInfos.stream()
                    .map(postImageInfo -> postImageInfo.toEntity(savedPost))
                    .forEach(image -> savedPost.addImages(image));
        }

        return new PostDto(savedPost);
    }


    @Override
    public PostDto findById(Long communityId, Long postId, Member currentMember) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = getCommunity(communityId);

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        validateMembership(currentMember, community);

        // 게시글이 존재하지 않으면 PostNotJoinException 던진다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Not found post"));

        return new PostDto(post);
    }

    private void validateMembership(Member currentMember, Community community) {
        Boolean isJoinedMember = communityMemberRepository.existsByMemberAndCommunity(currentMember, community);
        if (!isJoinedMember) {
            throw new CommunityNotJoinException("Not joined member");
        }
    }

    private Community getCommunity(Long communityId) {
        return communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));
    }
}
