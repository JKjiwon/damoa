package hello.sns.web.exception.business;

public class CommunityNotJoinedException extends BusinessException {
    public CommunityNotJoinedException() {
    }

    public CommunityNotJoinedException(String message) {
        super(message);
    }

    public CommunityNotJoinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityNotJoinedException(Throwable cause) {
        super(cause);
    }
}
