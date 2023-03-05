package com.damoa.domain.post.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {

    @NotBlank(message = "댓글 내용 입력해주시기 바랍니다.")
    private String content;
}
