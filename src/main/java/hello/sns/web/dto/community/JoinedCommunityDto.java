package hello.sns.web.dto.community;


import hello.sns.domain.community.Community;
import hello.sns.domain.community.CommunityMember;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class JoinedCommunityDto {

    private Long communityId;

    private String name;

    private String thumbNailImagePath;

    private String introduction;

    private String owner;

    private String category;

    private String grade;

    private String joinedAt;

    public JoinedCommunityDto(CommunityMember communityMember) {

        Community community = communityMember.getCommunity();
        this.communityId = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.introduction = community.getIntroduction();
        this.owner = community.getOwner().getName();
        this.grade = communityMember.getMemberGrade().name();
        this.joinedAt = communityMember.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
    }
}
