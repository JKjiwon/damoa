package hello.sns.web.exception;

import hello.sns.web.dto.common.ErrorResponse;
import hello.sns.web.exception.business.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerBusinessException(BusinessException e, HttpServletRequest req) {
        log.error("handleBusinessException", e);
        ErrorResponse response = ErrorResponse.of(req, e.getHttpStatus(), e.getMessage());
        return new ResponseEntity(response, e.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handlerAccessDeniedException(AccessDeniedException e, HttpServletRequest req) {
        log.error("handlerAccessDeniedException", e);
        ErrorResponse response = ErrorResponse.of(req, e.getHttpStatus(), e.getMessage());
        return new ResponseEntity(response, e.getHttpStatus());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handlerInvalidEmail(ConstraintViolationException e, HttpServletRequest req) {
        log.error("handlerInvalidEmail", e);
        ErrorResponse response = ErrorResponse.of(req, HttpStatus.BAD_REQUEST, "Invalid Email");
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handlerBindException(BindException e,
                                              HttpServletRequest req) {
        log.error("handlerBindException", e);
        return getErrorResponseByBindingResult(req, e.getBindingResult(), HttpStatus.BAD_REQUEST, "Invalid value");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                HttpServletRequest req) {
        e.printStackTrace();
        return getErrorResponseByBindingResult(req, e.getBindingResult(), HttpStatus.BAD_REQUEST, "Invalid values");
    }

    private ErrorResponse getErrorResponseByBindingResult(HttpServletRequest req, BindingResult bindingResult, HttpStatus httpStatus, String message) {

        Map<String, String> errMap = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errMap.put(error.getField(), error.getDefaultMessage());
        }

        return new ErrorResponse(req, httpStatus, message, errMap);
    }
}
