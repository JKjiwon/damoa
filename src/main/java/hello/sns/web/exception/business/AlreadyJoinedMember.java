package hello.sns.web.exception.business;

public class AlreadyJoinedMember extends BusinessException {
    public AlreadyJoinedMember() {
    }

    public AlreadyJoinedMember(String message) {
        super(message);
    }

    public AlreadyJoinedMember(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyJoinedMember(Throwable cause) {
        super(cause);
    }
}
