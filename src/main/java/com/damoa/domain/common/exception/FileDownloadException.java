package com.damoa.domain.common.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class FileDownloadException extends BusinessException {
    public FileDownloadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "File is not downloaded");
    }
}
