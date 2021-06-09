package hello.sns.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends RuntimeException{

    private HttpStatus httpStatus;

    public AccessDeniedException(String message) {
        super(message);
        this.httpStatus = HttpStatus.FORBIDDEN;
    }
}
