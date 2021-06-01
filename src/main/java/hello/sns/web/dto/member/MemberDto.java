package hello.sns.web.dto.member;

import hello.sns.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String name;
    private String profileImageName;
    private String profileImagePath;
    private String profileMessage;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.profileImageName = member.getProfileImageName();
        this.profileImagePath = member.getProfileImagePath();
        this.profileMessage = member.getProfileMessage();
    }
}