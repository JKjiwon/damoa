package hello.sns.config;

import hello.sns.service.MemberService;
import hello.sns.web.dto.member.CreateMemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class AppConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
//    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            MemberService memberService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                CreateMemberDto createMemberDto = CreateMemberDto.builder()
                        .name("user")
                        .email("user@email.com")
                        .password("user1234")
                        .build();
                memberService.join(createMemberDto);


                CreateMemberDto createMemberDto2 = CreateMemberDto.builder()
                        .name("user2")
                        .email("user2@email.com")
                        .password("user1234")
                        .build();
                memberService.join(createMemberDto2);
            }
        };
    }
}
