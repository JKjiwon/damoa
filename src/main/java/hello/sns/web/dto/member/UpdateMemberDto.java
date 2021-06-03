package hello.sns.web.dto.member;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UpdateMemberDto {

    @Length(min = 2, max = 40, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    @NotBlank(message = "성함을 입력해주시기 바랍니다.")
    private String name;

    private String profileMessage;

    private String thumbNailImageName;

    private String thumbNailImageUrl;

    private String mainImageName;

    private String mainImageUrl;
}
