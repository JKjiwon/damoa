package hello.sns.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {

    @NotBlank(message = "댓글 내용 입력해주시기 바랍니다.")
    private String content;
}
