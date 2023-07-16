package com.jagaldol.spring_security;

import com.jagaldol.spring_security._core.errors.exception.Exception401;
import com.jagaldol.spring_security._core.security.CustomUserDetails;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringSecurityApplication {

    @GetMapping("/tdd")
    public String tdd(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(userDetails.getUser().getId() + " : " + userDetails.getUser().getRoles());;
        return String.valueOf(userDetails.getId());
    }

    @GetMapping("/test/login")
    public String loginTest(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("/test/login=====================");
        try {
            System.out.println("authentication: " + userDetails.getUser());
        } catch(NullPointerException e) {
            throw new Exception401("인증 되지 않았습니다.");
        }

        return "세션 정보 확인";
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

}
