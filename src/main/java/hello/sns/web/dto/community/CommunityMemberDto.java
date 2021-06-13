package hello.sns.web.dto.community;

import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class CommunityMemberDto {
    private Long id;
    private String email;
    private String name;
    private String profileImagePath;
    private String grade;
    private String joinedAt;

    public CommunityMemberDto(CommunityMember communityMember) {
        Member member = communityMember.getMember();
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.profileImagePath = member.getProfileImagePath();
        this.grade = communityMember.getMemberGrade().name();
        this.joinedAt = communityMember.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
    }
}