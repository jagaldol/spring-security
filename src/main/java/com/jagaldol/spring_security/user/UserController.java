package com.jagaldol.spring_security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagaldol.spring_security._core.errors.exception.Exception401;
import com.jagaldol.spring_security._core.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserRequest.JoinDTO joinDTO) {
        // 1. 유효성 검사

        // 2. 회원가입 ( 서비스 요청)
        joinDTO.setPassword(passwordEncoder.encode(joinDTO.getPassword()));
        userRepository.save(joinDTO.toEntity());

        // 3. 응답
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO loginDTO) {

        try {
            // Authentication 객체 만들어서 return
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken); // UserDetailsService -> loadUserByUsername() 실행됨
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            System.out.println("customUserDetails : " + customUserDetails.getUser().getUsername());

            // 세션 만들기
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new Exception401("인증되지 않았습니다.");
        }
        return ResponseEntity.ok().body("성공!");
    }
}
