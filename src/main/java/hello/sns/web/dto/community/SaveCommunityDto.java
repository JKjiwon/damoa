package hello.sns.web.dto.community;

import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class SaveCommunityDto {

    @Length(min = 2, max = 40, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    @NotBlank(message = "커뮤니티 이름을 입력해주시기 바랍니다.")
    private String name;

    @Length(min = 10, message = "최소 {min}자 이상으로 입력해주시기 바랍니다.")
    @NotBlank(message = "커뮤니티 소개를 입력해주시기 바랍니다.")
    private String introduction;

    @NotNull
    private Member owner;

    public Community toEntity() {
        return Community.builder()
                .name(name)
                .introduction(introduction)
                .owner(owner)
                .build();
    }
}
