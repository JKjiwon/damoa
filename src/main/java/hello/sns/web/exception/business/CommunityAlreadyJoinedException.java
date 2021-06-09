package hello.sns.web.exception.business;

public class CommunityAlreadyJoinedException extends BusinessException {
    public CommunityAlreadyJoinedException() {
    }

    public CommunityAlreadyJoinedException(String message) {
        super(message);
    }

    public CommunityAlreadyJoinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityAlreadyJoinedException(Throwable cause) {
        super(cause);
    }
}
