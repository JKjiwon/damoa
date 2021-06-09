package hello.sns.web.dto.post;

import hello.sns.domain.community.Community;
import hello.sns.domain.member.Member;
import hello.sns.domain.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostDto {

    @Length(min = 1, max = 40, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    @NotBlank(message = "게시글 제목을 입력해주시기 바랍니다.")
    private String title;

    @Length(min = 10, message = "최소 {min}자 이상으로 입력해주시기 바랍니다.")
    @NotBlank(message = "게시글 내용을 입력해주시기 바랍니다.")
    private String content;


    public Post toEntity(Member writer, Community community) {
        return Post.builder()
                .writer(writer)
                .community(community)
                .title(title)
                .content(content)
                .build();
    }
}
