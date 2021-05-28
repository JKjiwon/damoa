package hello.sns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내서버가 응답을 할 때 json 을 자바스크립트에서 처리 할수 있게 할 지 설정하는 것
        config.addAllowedOrigin("*"); // 모든 ip 에 응답을 허용하겠다
        config.addAllowedHeader("*"); // 모든 header 에 응답을 허용하겠다
        config.addAllowedMethod("*"); // 모든 method 를 허용하겠다
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}