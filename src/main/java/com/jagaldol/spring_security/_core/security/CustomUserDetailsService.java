package com.jagaldol.spring_security._core.security;

import com.jagaldol.spring_security.user.User;
import com.jagaldol.spring_security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 컴퍼넌트 스캔시에 new CustomUserDetailsService() -> IoC 던져짐
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // login 호출
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOP = userRepository.findByUsername(username);

        if (userOP.isPresent()) {
            return new CustomUserDetails(userOP.get());
        }
        else {
            return null;
        }
    }
}
