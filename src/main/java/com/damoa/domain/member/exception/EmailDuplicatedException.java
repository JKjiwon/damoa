package com.damoa.domain.member.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailDuplicatedException extends BusinessException {
    public EmailDuplicatedException() {
        super(HttpStatus.BAD_REQUEST, "Email is duplicated.");
    }
}
