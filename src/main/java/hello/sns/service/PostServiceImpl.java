package hello.sns.service;

import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Post;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.CommunityNotFoundException;
import hello.sns.web.exception.business.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final CommunityMemberService communityMemberService;

    private final FileService fileService;

    private final ImageService imageService;

    @Transactional
    @Override
    public PostDto create(Long communityId, Member currentMember,
                          CreatePostDto createPostDto, List<MultipartFile> postImageFiles) {

        // 커뮤니티가 존재하지 않으면 CommunityNotFoundException 던진다.
        Community community = getCommunity(communityId);

        // 가입된 회원이 아니라면 CommunityNotJoinException 던진다.
        communityMemberService.validateMembership(currentMember.getId(), communityId);

        // 게시글 생성
        Post post = createPostDto.toEntity(currentMember, community);
        Post savedPost = postRepository.save(post);

        // 사진 업로드 - 게시글과 사진은 생명주가기 같다. -> Cascade.Persist 로 설정
        if (postImageFiles != null && !postImageFiles.isEmpty()) {
            List<PostImageInfo> postImageInfos = fileService.uploadPostImages(postImageFiles);
            postImageInfos.stream()
                    .map(postImageInfo -> postImageInfo.toEntity(savedPost))
                    .forEach(image -> savedPost.addImages(image));
        }

        return new PostDto(savedPost);
    }

    @Transactional
    @Override
    public void delete(Long communityId, Long postId, Member currentMember) {

        CommunityMember communityMember = communityMemberService.getMemberShip(currentMember.getId(), communityId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Not found post"));

        // 게시글 작성자 이거나, Admin, Owner 등급이라면 삭제 가능
        if (!post.writtenBy(currentMember) && !communityMember.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Owner, Admin, 작성자만 삭제 할 수 있습니다.");
        }

        // 게시글과 관련된 사진을 모두 삭제하고 게시글 삭제.
        post.deleteImages(fileService, imageService);
        postRepository.deleteById(postId);
    }


    @Override
    public PostDto findById(Long communityId, Long postId, Member currentMember) {

        // 게시글이 존재하지 않으면 PostNotJoinException 던진다.
        Post post = postRepository.findByIdAndCommunityId(postId, communityId)
                .orElseThrow(() -> new PostNotFoundException("Not found post"));

        // 내가 좋아요 한 게시글 표시
        return new PostDto(post);
    }

    @Override
    public Page<PostDto> findByAll(Long communityId, Member currentMember, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByCommunityId(communityId, pageable);
        return posts.map(post -> new PostDto(post));
    }

    private Community getCommunity(Long communityId) {
        return communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException("Not found community"));
    }
}
