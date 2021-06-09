package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CommunityNameDuplicatedException extends BusinessException {

    public CommunityNameDuplicatedException() {
        super(HttpStatus.BAD_REQUEST, "Community name is duplicated.");
    }
}
