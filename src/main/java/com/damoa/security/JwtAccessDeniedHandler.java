package com.damoa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.damoa.web.dto.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 필요한 권한이 없는 사용자가 리소스를 요청할 경우 403.
 */

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        log.error("Responding with unauthorized error. Message - {}", e.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("Application/json");
        response.setCharacterEncoding("utf-8");
        ErrorResponse errorResponse = ErrorResponse.of(request, HttpStatus.FORBIDDEN, e.getMessage());
        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}
