package hello.sns.web.exception;

public class FileUploadException extends RuntimeException{
    public FileUploadException() {
    }
    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUploadException(Throwable cause) {
        super(cause);
    }
}
