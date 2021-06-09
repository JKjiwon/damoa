package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FileUploadException extends BusinessException {

    public FileUploadException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
