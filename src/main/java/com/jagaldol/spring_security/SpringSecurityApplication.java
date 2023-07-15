package com.jagaldol.spring_security;

import com.jagaldol.spring_security._core.security.CustomUserDetails;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

}
