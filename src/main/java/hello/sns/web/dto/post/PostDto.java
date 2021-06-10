package hello.sns.web.dto.post;

import hello.sns.domain.member.Member;
import hello.sns.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;

    private String content;

    private String community;

    private PostWriterDto writer;

    private List<ImageDto> images;

    public PostDto(Post post) {

        this.id = post.getId();

        this.content = post.getContent();

        this.community = post.getCommunity().getName();

        this.writer = new PostWriterDto(post.getWriter());

        this.images = post.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
    }

    @NoArgsConstructor
    @Data
    public static class PostWriterDto {
        private Long id;
        private String name;

        public PostWriterDto(Member member) {
            this.id = member.getId();
            this.name = member.getName();
        }
    }
}
