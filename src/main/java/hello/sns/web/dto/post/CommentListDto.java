package hello.sns.web.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CommentListDto {
    private Long id;

    private String content;

    private Long postId;

    private CommentWriterDto writer;

    private Integer countOfSubComments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;



    public CommentListDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPost().getId();
        this.writer = new CommentWriterDto(comment.getWriter());
        this.createdAt = comment.getCreatedAt();
        this.countOfSubComments = comment.getChild().size();
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
