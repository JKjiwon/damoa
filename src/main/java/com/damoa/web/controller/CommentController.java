package com.damoa.web.controller;

import com.damoa.common.PageableValidator;
import com.damoa.domain.member.entity.Member;
import com.damoa.domain.post.sevice.CommentService;
import com.damoa.web.dto.common.CurrentMember;
import com.damoa.domain.post.dto.CommentDto;
import com.damoa.domain.post.dto.CommentListDto;
import com.damoa.domain.post.dto.CreateCommentDto;
import com.damoa.domain.post.dto.UpdateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    private final PageableValidator pageableValidator;

    @PostMapping
    public ResponseEntity create(HttpServletRequest httpServletRequest,
                                 @PathVariable Long communityId,
                                 @PathVariable Long postId,
                                 @RequestBody @Valid CreateCommentDto dto,
                                 @CurrentMember Member currentMember) throws URISyntaxException {

        CommentDto commentDto = commentService.create(communityId, postId, dto, currentMember);
        URI uri = new URI(httpServletRequest.getRequestURL().toString() + commentDto.getId());
        return ResponseEntity.created(uri).body(commentDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity delete(@PathVariable Long communityId,
                                 @PathVariable Long postId,
                                 @PathVariable Long commentId,
                                 @CurrentMember Member currentMember) {

        commentService.delete(communityId, postId, commentId, currentMember);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity findAllByPostId(@PathVariable Long communityId,
                                          @PathVariable Long postId,
                                          Pageable pageable) {

        pageableValidator.validate(pageable, 100);
        Page<CommentListDto> comments = commentService.findAllByPostId(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity findOne(@PathVariable Long communityId,
                                  @PathVariable Long postId,
                                  @PathVariable Long commentId) {

        CommentDto comment = commentService.findOneWithAllSubComment(postId, commentId);
        return ResponseEntity.ok(comment);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity update(@PathVariable Long communityId,
                                 @PathVariable Long postId,
                                 @PathVariable Long commentId,
                                 @Validated @RequestBody UpdateCommentDto dto,
                                 @CurrentMember Member currentMember) {

        CommentDto commentDto = commentService.update(communityId, postId, commentId, dto, currentMember);
        return ResponseEntity.ok().body(commentDto);
    }
}
