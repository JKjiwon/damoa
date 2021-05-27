package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.entity.member.MemberRole;
import hello.sns.repository.MemberRepository;
import hello.sns.web.dto.request.JoinRequest;
import hello.sns.web.exception.EmailDuplicatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(JoinRequest joinRequest) {
        validateDuplicateEmail(joinRequest); // 중복 이메일 검증
        Member member = joinRequest.toEntity();
        member.passwordEncoding(passwordEncoder.encode(member.getPassword()));
        member.addRole(MemberRole.USER);
        return memberRepository.save(member);
    }

    private void validateDuplicateEmail(JoinRequest joinRequest) {
        if (memberRepository.existsByEmail(joinRequest.getEmail())) {
            throw new EmailDuplicatedException("이미 존재하는 이메일 입니다.");
        }
    }
}
