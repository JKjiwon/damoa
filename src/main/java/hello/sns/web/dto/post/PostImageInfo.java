package hello.sns.web.dto.post;

import hello.sns.domain.post.Image;
import hello.sns.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostImageInfo {

    private String name;

    private String path;

    private int seq;

    public Image toEntity(Post post) {
        return Image.builder()
                .post(post)
                .name(name)
                .path(path)
                .seq(seq)
                .build();
    }
}
