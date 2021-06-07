package hello.sns.service;

import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Image;
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
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        CommunityMember communityMember = communityMemberRepository.findByMemberAndCommunity(currentMember, community)
                .orElseThrow(() -> new CommunityNotJoinException("Not joined member"));

        // 게시글 생성
        Post savedPost = createPostDto.toEntity(currentMember, community);
        postRepository.save(savedPost);

        // 사진 업로드 - 게시글과 사진은 생명주가기 같다. -> Cascade.All 로 설정
        if (!postImageFiles.isEmpty()) {
            if (postImageFiles.size() == 1) {
                PostImageInfo postImageInfo = fileService.uploadPostImage(postImageFiles.get(0));
                Image image = postImageInfo.toEntity(savedPost);
                savedPost.addImages(image);
            } else {
                List<PostImageInfo> postImageInfos = fileService.uploadPostImages(postImageFiles);
                postImageInfos.stream()
                        .map(postImageInfo -> postImageInfo.toEntity(savedPost))
                        .forEach(savedPost::addImages);
            }
        }

        return new PostDto(savedPost);
    }

    @Override
    public PostDto findById(Long communityId, Long postId, Member currentMember) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        CommunityMember communityMember = communityMemberRepository.findByMemberAndCommunity(currentMember, community)
                .orElseThrow(() -> new CommunityNotJoinException("Not joined member"));

        // 게시글이 존재하지 않으면 PostNotJoinException 던진다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Not found post"));

        return new PostDto(post);
    }
}
