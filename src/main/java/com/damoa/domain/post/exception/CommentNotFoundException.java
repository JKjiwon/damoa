package com.damoa.domain.post.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BusinessException {
    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND,"Comment is not found.");
    }
}
