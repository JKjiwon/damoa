package com.damoa.domain.community.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CommunityNameDuplicatedException extends BusinessException {

    public CommunityNameDuplicatedException() {
        super(HttpStatus.BAD_REQUEST, "Community name is duplicated.");
    }
}
