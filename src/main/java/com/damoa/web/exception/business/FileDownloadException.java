package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class FileDownloadException extends BusinessException {
    public FileDownloadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "File is not downloaded");
    }
}
