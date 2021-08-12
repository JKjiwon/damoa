package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class NotImageFileException extends BusinessException {
    public NotImageFileException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
