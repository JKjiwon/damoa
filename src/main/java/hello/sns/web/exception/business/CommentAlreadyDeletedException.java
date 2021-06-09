package hello.sns.web.exception.business;

public class CommentAlreadyDeletedException extends BusinessException{
    public CommentAlreadyDeletedException() {
    }

    public CommentAlreadyDeletedException(String message) {
        super(message);
    }

    public CommentAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentAlreadyDeletedException(Throwable cause) {
        super(cause);
    }
}
