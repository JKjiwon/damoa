package hello.sns.web.exception.validator;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PagingBadParameterException extends RuntimeException{
    private HttpStatus httpStatus;

    public PagingBadParameterException(Integer pageSize) {
        super(String.format("BadPagingParameter: Must page >= 0, size <= %d", pageSize));
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
