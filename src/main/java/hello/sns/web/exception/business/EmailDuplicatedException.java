package hello.sns.web.exception.business;

import hello.sns.web.exception.business.BusinessException;

public class EmailDuplicatedException extends BusinessException {
    public EmailDuplicatedException() {
    }

    public EmailDuplicatedException(String message) {
        super(message);
    }

    public EmailDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailDuplicatedException(Throwable cause) {
        super(cause);
    }
}
