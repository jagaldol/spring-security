package com.jagaldol.spring_security._core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagaldol.spring_security.user.UserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // /login POST 요청일 때 동작
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        try {
            // json으로 object mapping
            UserRequest.LoginDTO loginDTO = om.readValue(request.getInputStream(), UserRequest.LoginDTO.class);

            // Authentication 객체 만들어서 return
            UsernamePasswordAuthenticationToken token =
                    UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword());

            return this.getAuthenticationManager().authenticate(token); // UserDetailsService -> loadUserByUsername() 실행됨

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // UserDetailsService가 UserDetails를 잘 리턴하면!!
        System.out.println("로그인 성공함................................");
        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 인증서비스가 실행되다가 Exception 터지면
        System.out.println("로그인 중에 실패함...........................");
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
