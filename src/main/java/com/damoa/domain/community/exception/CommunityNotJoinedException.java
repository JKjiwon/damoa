package com.damoa.domain.community.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CommunityNotJoinedException extends BusinessException {
    public CommunityNotJoinedException() {
        super(HttpStatus.FORBIDDEN, "Member didn't join this community.");
    }
}
