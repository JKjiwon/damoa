package hello.sns.web.exception.business;

public class CommunityAlreadyJoinException extends BusinessException {
    public CommunityAlreadyJoinException() {
    }

    public CommunityAlreadyJoinException(String message) {
        super(message);
    }

    public CommunityAlreadyJoinException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityAlreadyJoinException(Throwable cause) {
        super(cause);
    }
}
