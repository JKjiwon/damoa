package com.damoa.domain.community.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CommunityNotFoundException extends BusinessException {
    public CommunityNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Community is not found.");
    }
}

