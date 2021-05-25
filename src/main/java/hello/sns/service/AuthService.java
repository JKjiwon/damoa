package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.entity.member.Role;
import hello.sns.entity.member.RoleName;
import hello.sns.web.dto.request.SignUpRequest;
import hello.sns.repository.MemberRepository;
import hello.sns.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Member join(SignUpRequest signUpRequest) {

        validateDuplicateUsername(signUpRequest); // 중복 회원 검증
        validateDuplicateEmail(signUpRequest); // 중복 이메일 검증

        Member member = signUpRequest.toEntity();
        member.passwordEncoding(passwordEncoder.encode(member.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("회원 Role 이 셋팅되지 않았습니다."));
        member.addRoles(Collections.singleton(userRole));

        return memberRepository.save(member);
    }

    private void validateDuplicateEmail(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일 입니다.");
        }
    }

    private void validateDuplicateUsername(SignUpRequest signUpRequest) {
        if (memberRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new IllegalStateException("이미 존재하는 아이디 입니다.");
        }
    }

    public List<Object> getMyInfoWithAuthorities() {
        return null;
    }
}
