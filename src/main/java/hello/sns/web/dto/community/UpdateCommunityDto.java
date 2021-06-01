package hello.sns.web.dto.community;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class UpdateCommunityDto {

    private String thumbNailImageName;

    private String thumbNailImageUrl;

    private String mainImageName;

    private String mainImageUrl;

    @Length(min = 10, message = "최소 {min}자 이상으로 입력해주시기 바랍니다.")
    @NotBlank(message = "커뮤니티 소개를 입력해주시기 바랍니다.")
    private String introduction;
}
