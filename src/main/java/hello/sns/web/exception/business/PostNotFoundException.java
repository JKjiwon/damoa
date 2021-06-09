package hello.sns.web.exception.business;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Post is not found.");
    }

}
