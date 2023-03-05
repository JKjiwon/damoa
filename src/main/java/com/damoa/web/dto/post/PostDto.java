package com.damoa.web.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.damoa.domain.community.entity.Community;
import com.damoa.domain.member.Member;
import com.damoa.domain.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {
    private Long id;

    private String content;

    private PostCommunityDto community;

    private PostWriterDto writer;

    private List<ImageDto> images;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public PostDto(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.community = new PostCommunityDto(post.getCommunity());
        this.writer = new PostWriterDto(post.getWriter());
        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());

        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PostWriterDto {
        private Long id;
        private String name;

        public PostWriterDto(Member member) {
            this.id = member.getId();
            this.name = member.getName();
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PostCommunityDto {
        private Long id;
        private String name;

        public PostCommunityDto(Community community) {
            this.id = community.getId();
            this.name = community.getName();
        }
    }
}
