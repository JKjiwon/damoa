package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Member is Not found.");
    }
}
