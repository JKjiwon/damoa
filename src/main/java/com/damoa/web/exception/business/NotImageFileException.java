package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotImageFileException extends BusinessException {
    public NotImageFileException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
