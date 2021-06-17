package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommunityNameDuplicatedException extends BusinessException {

    public CommunityNameDuplicatedException() {
        super(HttpStatus.BAD_REQUEST, "Community name is duplicated.");
    }
}
