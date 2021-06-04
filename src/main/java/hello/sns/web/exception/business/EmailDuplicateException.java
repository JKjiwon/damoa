package hello.sns.web.exception.business;

import hello.sns.web.exception.business.BusinessException;

public class EmailDuplicateException extends BusinessException {
    public EmailDuplicateException() {
    }

    public EmailDuplicateException(String message) {
        super(message);
    }

    public EmailDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailDuplicateException(Throwable cause) {
        super(cause);
    }
}
