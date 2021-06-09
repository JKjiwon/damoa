package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommentAlreadyDeletedException extends BusinessException {
    public CommentAlreadyDeletedException() {
        super(HttpStatus.BAD_REQUEST, "Comment has already deleted");
    }
}
