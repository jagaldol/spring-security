package com.jagaldol.spring_security._core.util;

import com.jagaldol.spring_security.user.User;
import com.jagaldol.spring_security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DBInit {
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDB(UserRepository userRepository) {
        return args -> {
            User user = User.builder()
                    .username("ssar")
                    .password(passwordEncoder.encode("1234"))
                    .email("ssar@nate.com")
                    .fullName("쌀")
                    .status(true)
                    .roles("ROLE_USER")
                    .build();
            userRepository.save(user);
        };
    }

}
