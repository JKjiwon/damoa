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
@RequestMapping("/api/communities/{communityId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity create(HttpServletRequest httpServletRequest,
                                 @PathVariable Long communityId,
                                 @Validated @RequestBody CreateCommentDto createCommentDto,
                                 @CurrentMember Member currentMember) throws URISyntaxException {

        Long commentId = commentService.create(communityId, createCommentDto, currentMember);

        URI uri = new URI(httpServletRequest.getRequestURI() + "/" + commentId);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity delete(@PathVariable Long communityId,
                                 @PathVariable Long commentId,
                                 @CurrentMember Member currentMember) {

        commentService.delete(communityId, commentId, currentMember);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}")
    public ResponseEntity findById(@PathVariable Long communityId,
                                   @PathVariable Long commentId,
                                   @CurrentMember Member currentMember) {

        CommentDto commentDto = commentService.findById(commentId, currentMember);
        return ResponseEntity.ok(commentDto);
    }
}
