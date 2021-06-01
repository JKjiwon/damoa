package hello.sns.web.dto.community;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import hello.sns.entity.member.MemberSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CommunityResponseDto {

    private Long id;

    private String name;

    private String thumbNailImageName;

    private String thumbNailImageUrl;

    private String mainImageName;

    private String mainImageUrl;

    private String introduction;

    @JsonSerialize(using = MemberSerializer.class)
    private Member admin;
}
