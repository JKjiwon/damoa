package hello.sns.common;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "member")
@Data
public class MemberProperties {

    @NotEmpty
    private String m1Email;

    @NotEmpty
    private String m1Password;

    @NotEmpty
    private String m1Name;

    @NotEmpty
    private String m2Email;

    @NotEmpty
    private String m2Password;

    @NotEmpty
    private String m2Name;
}
