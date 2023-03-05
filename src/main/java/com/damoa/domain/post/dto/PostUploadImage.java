package com.damoa.domain.post.dto;

import com.damoa.domain.post.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUploadImage {

    private String name;

    private String path;

    private int seq;

    public Image toEntity() {
        return Image.builder()
                .name(name)
                .path(path)
                .seq(seq)
                .build();
    }
}
