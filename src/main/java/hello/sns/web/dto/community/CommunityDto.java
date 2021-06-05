package hello.sns.web.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;
import hello.sns.entity.community.Community;
import lombok.Data;


@Data
public class CommunityDto {

    private Long communityId;

    private String name;

    private String thumbNailImageName;

    private String thumbNailImagePath;

    private String mainImageName;

    private String mainImagePath;

    private String introduction;

    private Long ownerId;

    private String category;

    @JsonProperty("isJoin")
    private boolean isJoin;

    public CommunityDto(Community community, boolean isJoin) {
        this.communityId = community.getId();
        this.name = community.getName();
        this.thumbNailImageName = community.getThumbNailImageName() != null ? community.getThumbNailImageName() : "";
        this.thumbNailImagePath = community.getThumbNailImagePath() != null ? community.getThumbNailImagePath() : "";
        this.mainImageName = community.getMainImageName() != null ? community.getMainImageName() : "";
        this.mainImagePath = community.getMainImagePath() != null ? community.getMainImagePath() : "";
        this.introduction = community.getIntroduction();
        this.ownerId = community.getOwner().getId();
        this.category = community.getCategory().getName();
        this.isJoin = isJoin;
    }
}
