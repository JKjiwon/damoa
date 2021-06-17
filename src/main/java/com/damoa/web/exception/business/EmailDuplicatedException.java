package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class EmailDuplicatedException extends BusinessException {
    public EmailDuplicatedException() {
        super(HttpStatus.BAD_REQUEST, "Email is duplicated.");
    }
}
