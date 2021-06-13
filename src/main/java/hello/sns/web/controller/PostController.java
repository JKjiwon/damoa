package hello.sns.web.controller;

import hello.sns.common.PageableValidator;
import hello.sns.domain.member.Member;
import hello.sns.service.PostService;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/posts")
@RestController
public class PostController {

    private final PostService postService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public ResponseEntity create(HttpServletRequest httpServletRequest,
                                 @PathVariable("communityId") Long communityId,
                                 @CurrentMember Member currentMember,
                                 CreatePostDto createPostDto,
                                 @RequestPart(value = "image", required = false) List<MultipartFile> images) throws URISyntaxException {

        Long postId = postService.create(communityId, currentMember, createPostDto, images);
        URI uri = new URI(httpServletRequest.getRequestURL().toString() + postId);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity findById(@PathVariable("communityId") Long communityId,
                                   @PathVariable("postId") Long postId,
                                   @CurrentMember Member currentMember) {
        PostDto postDto = postService.findById(communityId, postId, currentMember);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping
    public ResponseEntity findAll(@PathVariable("communityId") Long communityId,
                                  @CurrentMember Member currentMember,
                                  Pageable pageable) {
        pageableValidator.validate(pageable, 100);
        Page<PostDto> postDtos = postService.findByCommunityId(communityId, currentMember, pageable);
        return ResponseEntity.ok(postDtos);
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity delete(@PathVariable("communityId") Long communityId,
                                 @PathVariable("postId") Long postId,
                                 @CurrentMember Member currentMember) {
        postService.delete(communityId, postId, currentMember);

        return ResponseEntity.ok().build();
    }
}
