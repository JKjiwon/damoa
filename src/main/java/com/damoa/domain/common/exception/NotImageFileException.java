package com.damoa.domain.common.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotImageFileException extends BusinessException {
    public NotImageFileException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
