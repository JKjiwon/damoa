package hello.sns.web.controller;

import hello.sns.entity.member.Member;
import hello.sns.service.AuthService;
import hello.sns.web.dto.auth.JoinRequestDto;
import hello.sns.web.dto.auth.JwtAuthenticationResponse;
import hello.sns.web.dto.auth.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequestDto loginRequestDto) {
        String jwtToken = authService.login(loginRequestDto);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwtToken));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Validated JoinRequestDto joinRequestDto) {
        Member result = authService.join(joinRequestDto);
        return ResponseEntity.ok(result);
    }
}
