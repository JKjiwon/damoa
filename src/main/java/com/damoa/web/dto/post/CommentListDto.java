package com.damoa.web.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.damoa.domain.member.Member;
import com.damoa.domain.post.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentListDto {
    private Long id;

    private String content;

    private Long postId;

    private CommentWriterDto writer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    private Integer countOfSubComments;



    public CommentListDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPost().getId();
        this.writer = new CommentWriterDto(comment.getWriter());
        this.createdAt = comment.getCreatedAt();
        this.countOfSubComments = comment.getChild().size();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CommentWriterDto {
        private Long id;
        private String name;

        public CommentWriterDto(Member member) {
            this.id = member.getId();
            this.name = member.getName();
        }
    }
}
