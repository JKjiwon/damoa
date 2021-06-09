package hello.sns.web.controller;

import hello.sns.domain.member.Member;
import hello.sns.service.CommentService;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.post.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity create(@PathVariable Long communityId,
                                 @Validated @RequestBody CreateCommentDto createCommentDto,
                                 @CurrentMember Member currentMember) {

        commentService.create(communityId, createCommentDto, currentMember);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity delete(@PathVariable Long communityId,
                                 @PathVariable Long commentId,
                                 @CurrentMember Member currentMember) {

        commentService.delete(communityId, commentId, currentMember);

        return ResponseEntity.ok().build();
    }


}
