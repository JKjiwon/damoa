package hello.sns.web.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import hello.sns.web.dto.common.ErrorResponse;
import hello.sns.web.exception.business.BusinessException;
import hello.sns.web.exception.validator.PagingBadParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PagingBadParameterException.class)
    public ResponseEntity handlerPagingBadParameterException(PagingBadParameterException e, HttpServletRequest req) {
        log.error("handlerPagingBadParameterException", e);
        ErrorResponse response = ErrorResponse.of(req, e.getHttpStatus(), e.getMessage());
        return new ResponseEntity(response, e.getHttpStatus());
    }

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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        ErrorResponse response = ErrorResponse.of(req, HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity handlerBindException(BindException e,
                                               HttpServletRequest req) {
        log.error("handlerBindException", e);
        return getErrorResponseByBindingResult(req, e.getBindingResult(), HttpStatus.BAD_REQUEST, "Invalid value");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handlerMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                 HttpServletRequest req) {
        log.error("handlerMethodArgumentNotValidException", e);
        return getErrorResponseByBindingResult(req, e.getBindingResult(), HttpStatus.BAD_REQUEST, "Invalid values");
    }

    private ResponseEntity getErrorResponseByBindingResult(HttpServletRequest req, BindingResult bindingResult, HttpStatus httpStatus, String message) {
        ErrorResponse response = ErrorResponse.of(req, httpStatus, message, bindingResult);
        return new ResponseEntity(response, httpStatus);
    }

    @ExceptionHandler(InvalidFormatException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException e, HttpServletRequest req) {
        log.error("handleInvalidFormatException", e);
        ErrorResponse response = ErrorResponse.of(req, HttpStatus.BAD_REQUEST, "Invalid format value");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest req) {
        log.error("handleInvalidFormatException", e);
        ErrorResponse response = ErrorResponse.of(req, HttpStatus.BAD_REQUEST, "Invalid JSON format");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
