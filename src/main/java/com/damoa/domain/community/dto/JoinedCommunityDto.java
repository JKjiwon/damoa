package com.damoa.domain.community.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.damoa.domain.community.entity.Community;
import com.damoa.domain.community.entity.CommunityMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JoinedCommunityDto {

    private Long id;

    private String name;

    private String thumbNailImagePath;

    private String introduction;

    private String owner;

    private Long memberCount;

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
        this.memberCount = community.getMemberCount();
        this.grade = communityMember.getMemberGrade().name();
        this.joinedAt = communityMember.getJoinedAt();
    }
}
