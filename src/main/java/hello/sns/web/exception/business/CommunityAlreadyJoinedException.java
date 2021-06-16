package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommunityAlreadyJoinedException extends BusinessException {
    public CommunityAlreadyJoinedException() {
        super(HttpStatus.FORBIDDEN, "Member has already joined this community.");
    }
}
