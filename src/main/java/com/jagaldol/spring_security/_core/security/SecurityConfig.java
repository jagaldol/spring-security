package com.jagaldol.spring_security._core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
             AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new CustomUsernamePasswordAuthenticationFilter(authenticationManager));

            super.configure(builder);
        }
    }

    @Bean // 컴퍼넌트 스캔 시에 @Bean이 붙은 메서드가 있으면 실행시켜서 리턴되는 값을 IoC에 등록시키는 역할
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 해제
        http.csrf().disable();

        // UsernamePasswordAuthenticationFilter 가 작동안함 (Form Post x-www-form-urlencoded)
        http.formLogin().disable();

        // HttpBasicAuthenticationFilter (헤더에 username, password)
        http.httpBasic().disable();

        // 필터들을 갈아끼우는 내부 클래스
        http.apply(new CustomSecurityFilterManager());

        http.authorizeHttpRequests(authorize ->
                authorize.antMatchers("/")
                        .authenticated()
                        .anyRequest()
                        .permitAll());


        return http.build();
    }
}
