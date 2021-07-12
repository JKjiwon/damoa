package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class FileDownloadException extends BusinessException {
    public FileDownloadException() {
        super(HttpStatus.BAD_REQUEST, "File is not downloaded");
    }
}
