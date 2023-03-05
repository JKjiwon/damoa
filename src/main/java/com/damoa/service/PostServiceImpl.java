package com.damoa.service;

import com.damoa.domain.community.entity.Community;
import com.damoa.domain.community.entity.CommunityMember;
import com.damoa.domain.member.Member;
import com.damoa.domain.post.Comment;
import com.damoa.domain.post.Image;
import com.damoa.domain.post.Post;
import com.damoa.repository.CommentRepository;
import com.damoa.domain.community.repository.CommunityMemberRepository;
import com.damoa.repository.ImageRepository;
import com.damoa.repository.PostRepository;
import com.damoa.web.dto.post.CreatePostDto;
import com.damoa.web.dto.post.PostDto;
import com.damoa.web.dto.post.PostUploadImage;
import com.damoa.web.exception.AccessDeniedException;
import com.damoa.web.exception.business.CommunityNotJoinedException;
import com.damoa.web.exception.business.PostNotFoundException;
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
                          CreatePostDto dto, List<MultipartFile> postImageFiles) {
        CommunityMember writer = getMembership(currentMember, communityId);
        Post post = dto.toEntity(currentMember, writer.getCommunity());

        if (postImageFiles != null && !postImageFiles.isEmpty()) {
            List<PostUploadImage> postUploadImages = fileService.storePostImages(postImageFiles);
            postUploadImages.stream()
                    .map(PostUploadImage::toEntity)
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

        // 게시글과 관련된 사진을 모두 삭제
        post.getImages()
                .stream()
                .map(Image::getPath)
                .forEach(fileService::deleteFile);

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
