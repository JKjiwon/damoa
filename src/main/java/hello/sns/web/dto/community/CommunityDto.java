package hello.sns.web.dto.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CommunityDto {

    private Long id;

    private String name;

    private String thumbNailImageName;

    private String thumbNailImageUrl;

    private String mainImageName;

    private String mainImageUrl;

    private String introduction;

    private Long ownerId;
}
