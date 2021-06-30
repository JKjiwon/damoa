package com.damoa.web.dto.post;

import com.damoa.domain.post.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String downloadPath = "/api/images";

    public ImageDto(Image image) {
        this.id = image.getId();
        this.path = downloadPath + image.getPath();
        this.seq = image.getSeq();
    }
}
