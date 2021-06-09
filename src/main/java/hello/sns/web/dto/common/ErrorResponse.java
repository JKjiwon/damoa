package hello.sns.web.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map errorDetails;
	private String path;


	public ErrorResponse(HttpServletRequest req, HttpStatus httpStatus, String message, Map errorDetails) {
		this.status = httpStatus.value();
		this.error = httpStatus.name();
		this.message = message;
		this.errorDetails = errorDetails;
		this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
		this.path = req.getRequestURI();
	}

	public static ErrorResponse of(HttpServletRequest req, HttpStatus httpStatus, String message) {
		return new ErrorResponse(req, httpStatus, message, null);
	}

	public static ErrorResponse of(HttpServletRequest req, HttpStatus httpStatus, String message, Map errorDetails) {
		return new ErrorResponse(req, httpStatus, message, errorDetails);
	}
}
