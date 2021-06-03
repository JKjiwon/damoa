package hello.sns.web.exception;

public class EmailDuplicateException extends RuntimeException {
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
