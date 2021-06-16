package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommunityNotJoinedException extends BusinessException {
    public CommunityNotJoinedException() {
        super(HttpStatus.FORBIDDEN, "Member didn't join this community.");
    }
}
