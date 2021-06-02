package hello.sns.web.dto.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDetails<T> {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private T errorDetails;
	private String path;

	public ErrorResponseDetails(HttpServletRequest req, HttpStatus httpStatus, String message, T errorDetails) {
		this.status = httpStatus.value();
		this.error = httpStatus.name();
		this.message = message;
		this.errorDetails = errorDetails;
		this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
		this.path = req.getRequestURI();
	}
}
