package hello.sns.web.dto.post;

import hello.sns.entity.post.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
