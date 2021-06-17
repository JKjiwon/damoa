package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class FileDeleteException extends BusinessException {
    public FileDeleteException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "File is not deleted");
    }
}
