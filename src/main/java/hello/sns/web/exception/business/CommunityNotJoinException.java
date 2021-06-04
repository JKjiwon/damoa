package hello.sns.web.exception.business;

public class CommunityNotJoinException extends BusinessException {
    public CommunityNotJoinException() {
    }

    public CommunityNotJoinException(String message) {
        super(message);
    }

    public CommunityNotJoinException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityNotJoinException(Throwable cause) {
        super(cause);
    }
}
