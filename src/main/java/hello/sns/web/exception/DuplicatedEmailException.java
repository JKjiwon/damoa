package hello.sns.web.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException() {
    }

    public DuplicatedEmailException(String message) {
        super(message);
    }

    public DuplicatedEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedEmailException(Throwable cause) {
        super(cause);
    }
}
