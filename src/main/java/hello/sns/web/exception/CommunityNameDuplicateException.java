package hello.sns.web.exception;

public class CommunityNameDuplicateException extends RuntimeException {
    public CommunityNameDuplicateException() {
    }

    public CommunityNameDuplicateException(String message) {
        super(message);
    }

    public CommunityNameDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityNameDuplicateException(Throwable cause) {
        super(cause);
    }
}
