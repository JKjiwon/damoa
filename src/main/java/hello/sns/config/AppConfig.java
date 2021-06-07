package hello.sns.config;

import hello.sns.service.MemberService;
import hello.sns.service.MemberServiceImpl;
import hello.sns.web.dto.member.JoinMemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

//    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            MemberService memberService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                        .name("user")
                        .email("user@email.com")
                        .password("user1234")
                        .build();
                memberService.join(joinMemberDto);


                JoinMemberDto joinMemberDto2 = JoinMemberDto.builder()
                        .name("user2")
                        .email("user2@email.com")
                        .password("user1234")
                        .build();
                memberService.join(joinMemberDto2);
            }
        };
    }
}
