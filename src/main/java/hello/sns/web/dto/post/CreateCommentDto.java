package hello.sns.web.dto.post;

import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import hello.sns.domain.post.Post;
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

    @NotBlank(message = "댓글 내용 입력해주시기 바랍니다.")
    private String content;

    @NotNull(message = "게시글 번호를 입력해주시기 바랍니다.")
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
