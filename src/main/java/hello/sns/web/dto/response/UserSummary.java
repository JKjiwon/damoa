package hello.sns.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String name;
}