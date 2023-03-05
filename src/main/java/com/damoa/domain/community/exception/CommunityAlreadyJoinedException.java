package com.damoa.domain.community.exception;

import com.damoa.domain.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CommunityAlreadyJoinedException extends BusinessException {
    public CommunityAlreadyJoinedException() {
        super(HttpStatus.FORBIDDEN, "Member has already joined this community.");
    }
}
