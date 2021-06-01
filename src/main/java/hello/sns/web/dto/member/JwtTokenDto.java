package hello.sns.web.dto.member;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtTokenDto {

    private String tokenType = "Bearer";
    private String accessToken;

    public JwtTokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
