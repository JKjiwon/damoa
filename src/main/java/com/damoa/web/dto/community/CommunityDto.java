package com.damoa.web.dto.community;

import com.damoa.domain.community.Community;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityDto {

    private Long id;

    private String name;

    private String thumbNailImagePath;

    private String mainImagePath;

    private String introduction;

    private String owner;

    private String category;

    private Long memberCount;

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
        this.owner = community.getOwner().getName();
        this.category = community.getCategory().getName();
        this.memberCount = community.getMemberCount();
        this.isJoin = joinedCommunities.contains(community);
        this.createdAt = community.getCreatedAt();
    }

    public CommunityDto(Community community) {
        this.id = community.getId();
        this.name = community.getName();
        this.thumbNailImagePath = community.getThumbNailImagePath();
        this.mainImagePath = community.getMainImagePath();
        this.introduction = community.getIntroduction();
        this.owner = community.getOwner().getName();
        this.category = community.getCategory().getName();
        this.memberCount = community.getMemberCount();
        this.isJoin = true;
        this.createdAt = community.getCreatedAt();
    }
}
