package hello.sns.config;

import hello.sns.service.AuthService;
import hello.sns.web.dto.request.JoinRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AuthService authService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                JoinRequest joinRequest = JoinRequest.builder()
                        .name("user")
                        .email("user@email.com")
                        .password("user1234")
                        .build();
                authService.join(joinRequest);
            }
        };
    }
}
