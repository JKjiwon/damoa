package hello.sns.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSummary {
    private Long id;
    private String email;
    private String name;
}