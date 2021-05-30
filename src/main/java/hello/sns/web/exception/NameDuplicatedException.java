package hello.sns.web.exception;

public class NameDuplicatedException extends RuntimeException {
    public NameDuplicatedException() {
    }

    public NameDuplicatedException(String message) {
        super(message);
    }

    public NameDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameDuplicatedException(Throwable cause) {
        super(cause);
    }
}
