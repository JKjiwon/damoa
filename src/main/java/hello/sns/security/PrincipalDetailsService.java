package hello.sns.security;

import hello.sns.domain.member.Member;
import hello.sns.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * DB에 사용자 확인, 정보 제공
 */

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        // 이메일로 인증 요청
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(email));
        return new PrincipalDetails(member);
    }

    // JWTAuthenticationFilter 에서 사용
    @Transactional
    public UserDetails loadUserById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.valueOf(id))
        );

        return new PrincipalDetails(member);
    }
}