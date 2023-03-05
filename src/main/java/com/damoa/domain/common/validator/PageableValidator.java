package com.damoa.domain.common.validator;


import com.damoa.domain.common.exception.PagingBadParameterException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageableValidator {
    public void validate(Pageable pageable, Integer pageSize) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        if (page < 0 || size > pageSize) {
            throw new PagingBadParameterException(pageSize);
        }
    }
}
