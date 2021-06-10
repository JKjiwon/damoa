package hello.sns.web.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private String content;

    private CommentWriterDto writer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long parentId;

    private String createdAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ChildCommentDto> subComments;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = new CommentWriterDto(comment.getWriter());
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.subComments = comment.getChild().stream()
                .map(ChildCommentDto::new)
                .collect(Collectors.toList());
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
    }

    @NoArgsConstructor
    @Data
    public static class ChildCommentDto {
        private Long id;

        private String content;

        private CommentWriterDto writer;

        private String createdAt;

        public ChildCommentDto(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.writer = new CommentWriterDto(comment.getWriter());
            this.createdAt = comment.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
        }
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
