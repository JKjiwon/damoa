package hello.sns.web.dto.community;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hello.sns.domain.community.Community;
import hello.sns.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityDto {

    private Long id;

    private String name;

    private String thumbNailImagePath;

    private String mainImagePath;

    private String introduction;

    private CommunityOwnerDto owner;

    private String category;

    @JsonProperty("isJoin")
    private boolean isJoin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public CommunityDto(Community community, List<Community> joinedCommunities) {
        this.id = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.mainImagePath = community.getMainImagePath();
        this.introduction = community.getIntroduction();
        this.owner = new CommunityOwnerDto(community.getOwner());
        this.category = community.getCategory().getName();
        this.isJoin = joinedCommunities.contains(community);
        this.createdAt = community.getCreatedAt();
    }

    public CommunityDto(Community community) {
        this.id = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.mainImagePath = community.getMainImagePath();
        this.introduction = community.getIntroduction();
        this.owner = new CommunityOwnerDto(community.getOwner());
        this.category = community.getCategory().getName();
        this.isJoin = true;
        this.createdAt = community.getCreatedAt();
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
