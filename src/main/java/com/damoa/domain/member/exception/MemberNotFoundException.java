package com.damoa.domain.member.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Member is Not found.");
    }
}
