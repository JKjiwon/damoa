package hello.sns.web.dto;

import hello.sns.domain.member.MemberRole;

public class CommunityMemberDto {

    private Long id;
    private Long name;
    private String email;
    private MemberRole role = MemberRole.USER;
    private String profileImagePath;
    private String profileMessage;
}
