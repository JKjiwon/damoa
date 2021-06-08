package hello.sns.web.dto.common;

import lombok.*;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    @Builder
    public ErrorResponse(HttpServletRequest req, HttpStatus httpStatus, String message) {
        this.status = httpStatus.value();
        this.error = httpStatus.name();
        this.message = message != null ? message : "";
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
        this.path = req.getRequestURI();
    }
}
