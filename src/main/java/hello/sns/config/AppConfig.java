package hello.sns.config;

import hello.sns.common.MemberProperties;
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

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            MemberService memberService;

            @Autowired
            MemberProperties memberProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                CreateMemberDto createMemberDto = CreateMemberDto.builder()
                        .name(memberProperties.getM1Name())
                        .email(memberProperties.getM1Email())
                        .password(memberProperties.getM1Password())
                        .build();
                memberService.create(createMemberDto);

                CreateMemberDto createMemberDto2 = CreateMemberDto.builder()
                        .name(memberProperties.getM2Name())
                        .email(memberProperties.getM2Email())
                        .password(memberProperties.getM2Password())
                        .build();
                memberService.create(createMemberDto2);
            }
        };
    }
}
