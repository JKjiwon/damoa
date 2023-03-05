package com.damoa.domain.post.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Post is not found.");
    }

}
