package hello.sns.web.exception;

import hello.sns.web.dto.common.ErrorResponse;
import hello.sns.web.dto.common.ErrorResponseDetails;
import hello.sns.web.exception.business.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
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
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public ErrorResponse handlerBusinessException(BusinessException e, HttpServletRequest req) {
        e.printStackTrace();
        return new ErrorResponse(req, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponseDetails handlerBindException(BindException e,
                                                     HttpServletRequest req) {
        e.printStackTrace();
        return getErrorResponseByBindingResult(req, e.getBindingResult(), HttpStatus.BAD_REQUEST, "유효하지 않은 값이 있습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDetails handlerMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                       HttpServletRequest req) {
        e.printStackTrace();
        return getErrorResponseByBindingResult(req, e.getBindingResult(), HttpStatus.BAD_REQUEST, "유효하지 않은 값이 있습니다.");
    }

    private ErrorResponseDetails getErrorResponseByBindingResult(HttpServletRequest req, BindingResult bindingResult, HttpStatus httpStatus, String message) {

        Map<String, String> errMap = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errMap.put(error.getField(), error.getDefaultMessage());
        }

        return new ErrorResponseDetails(req, httpStatus, message, errMap);
    }
}
