package com.jagaldol.spring_security._core.security;

import com.jagaldol.spring_security._core.errors.exception.Exception401;
import com.jagaldol.spring_security._core.errors.exception.Exception403;
import com.jagaldol.spring_security._core.util.FilterResponseUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            // builder.addFilter(new CustomUsernamePasswordAuthenticationFilter(authenticationManager));

            super.configure(builder);
        }
    }

    @Bean // 컴퍼넌트 스캔 시에 @Bean이 붙은 메서드가 있으면 실행시켜서 리턴되는 값을 IoC에 등록시키는 역할
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 해제
        // SSR으로 스프링이 직접 페이지 만들어서 줄때는 csrf를 붙이는게 좋지만 프론트가 분리되어 있다면 그냥 해제하는게 낫다.
        // Postman 에서 테스트할때도 csrf.disable 을 하지 않으면 에러가 발생한다.
        http.csrf().disable();

        // 2. iframe 거부
        http.headers().frameOptions().sameOrigin();

        // 3. cors 재설정
        http.cors().configurationSource(configurationSource());

        // 4. jSessionId 가 응답이 될 때 세션영역에서 사라진다(JWT 로 stateless 하게 할거임)
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 5. form 로그인 해제해서 UsernamePasswordAuthenticationFilter 비활성화 시키기 (Form Post x-www-form-urlencoded)
        http.formLogin().disable();

        // 6. 로그인 인증창이 뜨지 않도록 HttpBasicAuthenticationFilter 비활성화 (헤더에 username, password)
        http.httpBasic().disable();

        // 7. 커스텀 필터들 적용 - 시큐리티 필터 커스텀으로 교체 (필터들을 갈아끼우는 내부 클래스)
        http.apply(new CustomSecurityFilterManager());

        // 8. 인증 실패 처리
        http.exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
            System.out.println("인증 실패");
            FilterResponseUtils.unAuthorized(response, new Exception401("인증되지 않았습니다."));
        }));

        // 9. 권한 실패 처리
        http.exceptionHandling().accessDeniedHandler((((request, response, accessDeniedException) -> {
            FilterResponseUtils.forbidden(response, new Exception403("권한이 없습니다."));
            System.out.println("권한 없음");
        })));

        // 10. 인증 권한 필터 설정
        http.authorizeRequests(authorize ->
                authorize.antMatchers("/account/**")
                        .authenticated()
                        .antMatchers("/admin/**")
                        .access("hasRole('ADMIN')")
                        .anyRequest()
                        .permitAll());


        return http.build();
    }

    private CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        // localhost:8080 백엔드, localhost:3000 프론트엔드
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용(나중에 프론트 엔드 IP만 허용하도록 변경해야함)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 예전엔 default 였지만, 지금은 설정 필요(JWT 토큰이 이 헤더에 담겨서 온다)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
