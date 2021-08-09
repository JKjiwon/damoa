package com.damoa.web.dto.post;

import com.damoa.domain.post.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageDto {
    private Long id;
    private String path;
    private int seq;

    public ImageDto(Image image) {
        this.id = image.getId();
        this.path = image.getPath();
        this.seq = image.getSeq();
    }
}
