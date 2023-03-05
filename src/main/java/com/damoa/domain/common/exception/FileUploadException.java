package com.damoa.domain.common.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class FileUploadException extends BusinessException {
    public FileUploadException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
