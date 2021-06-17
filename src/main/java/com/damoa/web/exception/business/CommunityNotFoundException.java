package com.damoa.web.exception.business;

import org.springframework.http.HttpStatus;

public class CommunityNotFoundException extends BusinessException {
    public CommunityNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Community is not found.");
    }
}

