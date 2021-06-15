package hello.sns.web.dto.post;

import hello.sns.domain.post.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageDto {
    private Long imageId;
    private String path;
    private int seq;

    public ImageDto(Image image) {
        this.imageId = image.getId();
        this.path = image.getPath();
        this.seq = image.getSeq();
    }
}
