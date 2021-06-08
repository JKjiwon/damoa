package hello.sns.web.dto.member;

import hello.sns.entity.member.Member;
import hello.sns.entity.member.MemberRole;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JoinMemberDto {

    @NotBlank(message = "이메일을 입력해주시기 바랍니다.")
    @Email
    private String email;

    @Length(min = 7, max = 20, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    @NotBlank(message = "비밀번호를 입력해주시기 바랍니다.")
    private String password;

    @Length(min = 2, max = 40, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    @NotBlank(message = "성함을 입력해주시기 바랍니다.")
    private String name;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .password(password)
                .role(MemberRole.USER)
                .build();
    }
}
