package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommunityNotJoinedException extends BusinessException {
    public CommunityNotJoinedException() {
        super(HttpStatus.BAD_REQUEST, "Member did join this community.");
    }
}