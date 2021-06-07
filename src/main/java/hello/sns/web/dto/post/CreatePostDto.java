package hello.sns.web.dto.post;

import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CreatePostDto {

    @Length
    @NotBlank(message = "게시글 제목을 입력해주시기 바랍니다.")
    private String title;

    @Length
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
