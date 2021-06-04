package hello.sns.web.exception;

public class CommunityNotFoundException extends BusinessException {
    public CommunityNotFoundException() {
    }

    public CommunityNotFoundException(String message) {
        super(message);
    }

    public CommunityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityNotFoundException(Throwable cause) {
        super(cause);
    }
}

