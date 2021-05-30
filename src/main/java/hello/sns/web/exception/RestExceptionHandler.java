package hello.sns.web.exception;

import hello.sns.web.dto.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailDuplicatedException.class)
    public ErrorResponse handlerEmailDuplicatedException(EmailDuplicatedException e, HttpServletRequest req) {
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                HttpServletRequest req) {
        e.printStackTrace();

        return getErrorResponseByBindingResult(e.getBindingResult(), HttpStatus.BAD_REQUEST, "유효하지 않은 값이 있습니다.");
    }

    private ErrorResponse getErrorResponseByBindingResult(BindingResult bindingResult, HttpStatus httpStatus, String message) {

        Map<String, String> errMap = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errMap.put(error.getField(), error.getDefaultMessage());
        }

        return new ErrorResponse(httpStatus, message, errMap);
    }
}
