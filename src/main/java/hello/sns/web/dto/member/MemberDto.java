package hello.sns.web.dto.member;

import hello.sns.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String name;
    private String profileImagePath;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.profileImagePath = member.getProfileImagePath();
    }
}