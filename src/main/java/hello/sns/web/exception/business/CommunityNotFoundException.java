package hello.sns.web.exception.business;

import hello.sns.web.exception.business.BusinessException;

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

