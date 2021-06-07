package hello.sns.web.dto.post;

import hello.sns.entity.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;

    private String title;

    private String content;

    private String community;

    private String writer;

    private List<ImageDto> images;

    public PostDto(Post post) {

        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();

        this.community = post.getCommunity().getName();

        this.writer = post.getWriter().getName();

        this.images = post.getImages().stream()
                .map(image -> new ImageDto(image))
                .collect(Collectors.toList());
    }
}
