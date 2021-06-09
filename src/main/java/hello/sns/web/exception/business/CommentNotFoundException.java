package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BusinessException{
    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND,"Comment is not found.");
    }
}
