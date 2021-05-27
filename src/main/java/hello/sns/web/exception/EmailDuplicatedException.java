package hello.sns.web.exception;

public class EmailDuplicatedException extends RuntimeException {
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
