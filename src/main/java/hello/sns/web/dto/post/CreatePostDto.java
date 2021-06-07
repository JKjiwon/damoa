package hello.sns.web.dto.post;

import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import hello.sns.entity.post.Post;
import lombok.Data;

@Data
public class CreatePostDto {

    private String title;
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
