package com.damoa.web.dto.community;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.damoa.domain.community.CommunityMember;
import com.damoa.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommunityMemberDto {

    private Long id;

    private String email;

    private String name;

    private String profileImagePath;

    private String grade;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinedAt;


    public CommunityMemberDto(CommunityMember communityMember) {
        Member member = communityMember.getMember();
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.profileImagePath = member.getProfileImagePath();
        this.grade = communityMember.getMemberGrade().name();
        this.joinedAt = communityMember.getJoinedAt();
    }
}