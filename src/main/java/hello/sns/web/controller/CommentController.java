package hello.sns.web.controller;

import hello.sns.domain.member.Member;
import hello.sns.service.CommentService;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.post.CommentDto;
import hello.sns.web.dto.post.CreateCommentDto;
import lombok.RequiredArgsConstructor;
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

        URI uri = new URI(httpServletRequest.getRequestURI() + "/" + commentId);
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

    @GetMapping("/{commentId}")
    public ResponseEntity findAllByPostId(@PathVariable Long communityId,
                                          @PathVariable Long postId,
                                          @PathVariable Long commentId,
                                          @CurrentMember Member currentMember) {

        CommentDto commentDto = commentService.findAllByPostId(postId, commentId, currentMember);
        return ResponseEntity.ok(commentDto);
    }
}
