package hello.sns.web.dto.post;

import hello.sns.entity.member.Member;
import hello.sns.entity.post.Comment;
import hello.sns.entity.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCommentDto {

    @NotBlank
    private String content;

    @NotNull
    private Long postId;

    private Long parentCommentId;

    public Comment toEntity(Post post, Comment parent, Member writer) {

        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .parent(parent)
                .writer(writer)
                .build();

        if (parent != null) {
            parent.addComment(comment);
        }

        return comment;
    }

    public boolean existsParentCommentId() {
        return parentCommentId != null;
    }
}
