package hello.sns.web.dto.community;


import com.fasterxml.jackson.annotation.JsonFormat;
import hello.sns.domain.community.Community;
import hello.sns.domain.community.CommunityMember;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class JoinedCommunityDto {

    private Long id;

    private String name;

    private String thumbNailImagePath;

    private String introduction;

    private String owner;

    private String category;

    private String grade;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinedAt;

    public JoinedCommunityDto(CommunityMember communityMember) {

        Community community = communityMember.getCommunity();
        this.id = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.introduction = community.getIntroduction();
        this.owner = community.getOwner().getName();
        this.category = community.getCategory().getName();
        this.grade = communityMember.getMemberGrade().name();
        this.joinedAt = communityMember.getJoinedAt();
    }
}
