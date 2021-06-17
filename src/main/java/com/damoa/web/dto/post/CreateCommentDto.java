package com.damoa.web.dto.post;

import com.damoa.domain.member.Member;
import com.damoa.domain.post.Comment;
import com.damoa.domain.post.Post;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCommentDto {

    @NotBlank(message = "댓글 내용 입력해주시기 바랍니다.")
    private String content;

    private Long parentCommentId;

    public Comment toEntity(Post post, Comment parent, Member writer) {

        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .writer(writer)
                .build();

        if (parent != null) {
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            comment.setParent(parent);
            comment.setLevel(2);
        } else {
            comment.setLevel(1);
        }

        return comment;
    }

    public boolean existsParentCommentId() {
        return parentCommentId != null;
    }
}
