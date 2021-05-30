package hello.sns.web.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {

    private String tokenType = "Bearer";
    private String accessToken;

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}
