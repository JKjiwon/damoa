package hello.sns.web.controller;

import hello.sns.domain.member.Member;
import hello.sns.service.CommentService;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.post.CommentDto;
import hello.sns.web.dto.post.CommentListDto;
import hello.sns.web.dto.post.CreateCommentDto;
import hello.sns.web.dto.post.UpdateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity create(HttpServletRequest httpServletRequest,
                                 @PathVariable Long communityId,
                                 @PathVariable Long postId,
                                 @Validated @RequestBody CreateCommentDto createCommentDto,
                                 @CurrentMember Member currentMember) throws URISyntaxException {

        Long commentId = commentService.create(communityId, postId, createCommentDto, currentMember);

        URI uri = new URI(httpServletRequest.getRequestURL().toString() + commentId);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity delete(@PathVariable Long communityId,
                                 @PathVariable Long postId,
                                 @PathVariable Long commentId,
                                 @CurrentMember Member currentMember) {

        commentService.delete(communityId, postId, commentId, currentMember);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity findAllByPostId(@PathVariable Long communityId,
                                          @PathVariable Long postId,
                                          @CurrentMember Member currentMember,
                                          Pageable pageable) {

        Page<CommentListDto> comments = commentService.findAllByPostId(postId, currentMember, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity findOne(@PathVariable Long communityId,
                                  @PathVariable Long postId,
                                  @PathVariable Long commentId,
                                  @CurrentMember Member currentMember) {

        CommentDto comment = commentService.findOneWithAllSubComment(postId, commentId, currentMember);
        return ResponseEntity.ok(comment);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity update(@PathVariable Long communityId,
                                 @PathVariable Long postId,
                                 @PathVariable Long commentId,
                                 @Validated @RequestBody UpdateCommentDto updateCommentDto,
                                 @CurrentMember Member currentMember) {
        commentService.update(communityId, commentId, updateCommentDto, currentMember);
        return ResponseEntity.ok().build();
    }
}
