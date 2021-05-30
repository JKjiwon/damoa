package hello.sns.web.dto.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse<T> {
	private int code;
	private String message;
	private T errorDetails;
	private String responseTime;

	public ErrorResponse(HttpStatus httpStatus, String message, T errorDetails) {
		this.code = httpStatus.value();
		this.message = message;
		this.errorDetails = errorDetails;
		this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
	}
}
