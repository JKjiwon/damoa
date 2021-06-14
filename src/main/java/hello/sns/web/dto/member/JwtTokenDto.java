package hello.sns.web.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtTokenDto {

    private String tokenType = "Bearer";

    private String accessToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime expiryDate;

    public JwtTokenDto(String accessToken, Date expiryDate) {
        this.accessToken = accessToken;
        this.expiryDate = Instant.ofEpochMilli(expiryDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
