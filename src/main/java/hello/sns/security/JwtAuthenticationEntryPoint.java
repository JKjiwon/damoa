package hello.sns.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.web.dto.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증되지 않은 사용자가 리소스를 요청할 경우 401
     */

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        log.error("Responding with unauthenticated error. Message - {}", e.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("Application/json");
        ErrorResponse errorResponse = new ErrorResponse(request, HttpStatus.UNAUTHORIZED, e.getMessage());
        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}