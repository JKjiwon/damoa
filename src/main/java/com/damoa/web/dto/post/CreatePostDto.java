package com.damoa.web.dto.post;

import com.damoa.domain.community.entity.Community;
import com.damoa.domain.member.Member;
import com.damoa.domain.post.Post;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostDto {

    @Length(min = 10, message = "최소 {min}자 이상으로 입력해주시기 바랍니다.")
    @NotBlank(message = "게시글 내용을 입력해주시기 바랍니다.")
    private String content;


    public Post toEntity(Member writer, Community community) {
        return Post.builder()
                .writer(writer)
                .community(community)
                .content(content)
                .build();
    }
}
