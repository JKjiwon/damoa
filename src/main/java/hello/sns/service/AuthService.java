package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.entity.member.MemberRole;
import hello.sns.repository.MemberRepository;
import hello.sns.security.JwtTokenProvider;
import hello.sns.web.dto.auth.JoinRequestDto;
import hello.sns.web.dto.auth.LoginRequestDto;
import hello.sns.web.exception.EmailDuplicatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public Member join(JoinRequestDto joinRequestDto) {
        validateDuplicateEmail(joinRequestDto.getEmail()); // 중복 이메일 검증
        Member member = joinRequestDto.toEntity();
        member.passwordEncoding(passwordEncoder.encode(member.getPassword()));
        member.addRole(MemberRole.USER);
        return memberRepository.save(member);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new EmailDuplicatedException("이미 존재하는 이메일 입니다.");
        }
    }

    public String login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }
}
