package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class FileUploadException extends BusinessException {
    public FileUploadException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
