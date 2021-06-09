package hello.sns.web.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<FieldError> errors;
	private String path;


	public ErrorResponse(HttpServletRequest req, HttpStatus httpStatus, String message, List<FieldError> errors) {
		this.status = httpStatus.value();
		this.error = httpStatus.name();
		this.message = message;
		this.errors = errors;
		this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM_dd hh:mm:ss"));
		this.path = req.getRequestURI();
	}

	public static ErrorResponse of(HttpServletRequest req, HttpStatus httpStatus, String message) {
		return new ErrorResponse(req, httpStatus, message, null);
	}

	public static ErrorResponse of(HttpServletRequest req, HttpStatus httpStatus, String message, BindingResult bindingResult) {
		return new ErrorResponse(req, httpStatus, message, FieldError.of(bindingResult));
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class FieldError {
		private String field;
		private String value;
		private String reason;

		private FieldError(final String field, final String value, final String reason) {
			this.field = field;
			this.value = value;
			this.reason = reason;
		}

		public static List<FieldError> of(final String field, final String value, final String reason) {
			List<FieldError> fieldErrors = new ArrayList<>();
			fieldErrors.add(new FieldError(field, value, reason));
			return fieldErrors;
		}

		private static List<FieldError> of(final BindingResult bindingResult) {
			final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
			return fieldErrors.stream()
					.map(error -> new FieldError(
							error.getField(),
							error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
							error.getDefaultMessage()))
					.collect(Collectors.toList());
		}
	}
}
