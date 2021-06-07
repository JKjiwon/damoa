package hello.sns.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostImageInfo {

    private long postId;

    private String imageName;

    private String imagePath;

    private int seq;
}
