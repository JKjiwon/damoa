package hello.sns.web.dto.member;

import hello.sns.domain.member.Member;
import hello.sns.domain.member.MemberRole;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


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
        password = new BCryptPasswordEncoder().encode(password);
        return Member.builder()
                .email(email)
                .name(name)
                .password(password)
                .role(MemberRole.USER)
                .build();
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
