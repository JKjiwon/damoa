package com.damoa.domain.member.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMemberDto {
    @Length(min = 2, max = 40, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    @NotBlank(message = "성함을 입력해주시기 바랍니다.")
    private String name;
}
