package hello.sns.web.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;
import hello.sns.domain.community.Community;
import hello.sns.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityDto {

    private Long communityId;

    private String name;

    private String thumbNailImagePath;

    private String mainImagePath;

    private String introduction;

    private CommunityOwnerDto owner;

    private String category;

    @JsonProperty("isJoin")
    private boolean isJoin;

    public CommunityDto(Community community, List<Community> joinedCommunities) {
        this.communityId = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.mainImagePath = community.getMainImagePath();
        this.introduction = community.getIntroduction();
        this.owner = new CommunityOwnerDto(community.getOwner());
        this.category = community.getCategory().getName();
        this.isJoin = joinedCommunities.contains(community);
    }

    public CommunityDto(Community community) {
        this.communityId = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.mainImagePath = community.getMainImagePath();
        this.introduction = community.getIntroduction();
        this.owner = new CommunityOwnerDto(community.getOwner());
        this.category = community.getCategory().getName();
        this.isJoin = true;
    }

    @Data
    @NoArgsConstructor
    static class CommunityOwnerDto {
        private Long id;
        private String name;

        public CommunityOwnerDto(Member member) {
            this.id = member.getId();
            this.name = member.getName();
        }
    }

}
