package hello.sns.web.dto.post;

import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CommentListDto {
    private Long id;

    private String content;

    private Long postId;

    private CommentWriterDto writer;

    private String createdAt;


    public CommentListDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPost().getId();
        this.writer = new CommentWriterDto(comment.getWriter());
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
    }

    @NoArgsConstructor
    @Data
    public static class CommentWriterDto {
        private Long id;
        private String name;

        public CommentWriterDto(Member member) {
            this.id = member.getId();
            this.name = member.getName();
        }
    }
}
