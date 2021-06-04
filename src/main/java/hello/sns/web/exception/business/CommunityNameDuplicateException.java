package hello.sns.web.exception.business;

import hello.sns.web.exception.business.BusinessException;

public class CommunityNameDuplicateException extends BusinessException {

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
