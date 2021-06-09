package hello.sns.web.exception.business;

import hello.sns.web.exception.business.BusinessException;

public class CommunityNameDuplicatedException extends BusinessException {

    public CommunityNameDuplicatedException() {
    }

    public CommunityNameDuplicatedException(String message) {
        super(message);
    }

    public CommunityNameDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityNameDuplicatedException(Throwable cause) {
        super(cause);
    }
}
