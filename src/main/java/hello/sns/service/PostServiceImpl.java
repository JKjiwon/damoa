package hello.sns.service;

import hello.sns.domain.community.Community;
import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import hello.sns.domain.post.Image;
import hello.sns.domain.post.Post;
import hello.sns.repository.CommentRepository;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.ImageRepository;
import hello.sns.repository.PostRepository;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.AccessDeniedException;
import hello.sns.web.exception.business.CommunityNotJoinedException;
import hello.sns.web.exception.business.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CommunityMemberRepository communityMemberRepository;

    private final FileService fileService;

    private final ImageRepository imageRepository;

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public PostDto create(Long communityId, Member currentMember,
                          CreatePostDto createPostDto, List<MultipartFile> postImageFiles) {

        CommunityMember writer = getMembership(currentMember, communityId);

        Post post = createPostDto.toEntity(currentMember, writer.getCommunity());

        // 사진 업로드 - 게시글과 사진은 생명주가기 같다. -> Cascade.All 로 설정
        if (postImageFiles != null && !postImageFiles.isEmpty()) {
            List<PostImageInfo> postImageInfos = fileService.uploadPostImages(postImageFiles);
            postImageInfos.stream()
                    .map(PostImageInfo::toEntity)
                    .forEach(post::addImages);
        }
        return new PostDto(postRepository.save(post));
    }

    @Transactional
    @Override
    public void delete(Long communityId, Long postId, Member currentMember) {

        CommunityMember actor = getMembership(currentMember, communityId);

        Post post = postRepository.findByIdWithAll(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.writtenBy(currentMember) && !actor.isOwnerOrAdmin()) {
            throw new AccessDeniedException("Not allowed member");
        }
        // 게시글과 관련된 사진을 모두 삭제하고 게시글 삭제.
        post.getImages()
                .stream()
                .map(Image::getPath)
                .forEach(fileService::deleteFile);

        // Cascade.ALL로 설정할 시 Post 1개에 여러개의 Image이 있을 때 Post를 삭제하면 Image 개수 만큼 삭제 쿼리가 나간다.
        // Image 삭제를 벌크 연산으로 쿼리 1번에 해결.
        imageRepository.deleteByPostId(postId);

        // 게시글과 관련된 댓글 모두 삭제
        List<Comment> comments = commentRepository.findByPostIdAndLevel(postId, 1);
        comments.forEach(
                comment -> {
                    Long id = comment.getId();
                    commentRepository.deleteByParentId(id);
                    commentRepository.deleteById(id);
                }
        );
        postRepository.deleteById(postId);
    }

    @Override
    public PostDto findById(Long communityId, Long postId, Member currentMember) {
        Post post = postRepository.findByIdAndCommunityId(postId, communityId)
                .orElseThrow(PostNotFoundException::new);
        return new PostDto(post);
    }

    @Override
    public Page<PostDto> findAllByCommunityId(Long communityId, Member currentMember, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByCommunityIdOrderByIdDesc(communityId, pageable);

        return posts.map(PostDto::new);
    }

    @Override
    public Page<PostDto> findAllByMember(Member currentMember, Pageable pageable) {
        List<CommunityMember> memberships = communityMemberRepository.findByMember(currentMember);
        List<Community> communities = memberships.stream().map(CommunityMember::getCommunity)
                .collect(Collectors.toList());

        Page<Post> posts = postRepository.findByCommunityInOrderByIdDesc(pageable, communities);

        return posts.map(PostDto::new);
    }

    private CommunityMember getMembership(Member currentMember, Long communityId) {
        return communityMemberRepository.findByMemberAndCommunityId(currentMember, communityId)
                .orElseThrow(CommunityNotJoinedException::new);
    }
}
