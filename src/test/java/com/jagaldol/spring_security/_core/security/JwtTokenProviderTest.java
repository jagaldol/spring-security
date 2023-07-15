package com.jagaldol.spring_security._core.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.jagaldol.spring_security.user.User;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    public void create_test() {
        User user = User.builder()
                .id(1L)
                .username("ssar")
                .password("1234")
                .email("ssar@nate.com")
                .fullName("쌀")
                .status(true)
                .roles("ROLE_USER")
                .build();
        String jwt = JwtTokenProvider.create(user);
        System.out.println(jwt);
    }

    @Test
    public void verify_test() {
        DecodedJWT decodedJWT = JwtTokenProvider.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzc2FyQG5hdGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlkIjoxLCJleHAiOjE2ODk2MTQ4MDR9.HvAgYtZvTaDpOfLEalh_BWz29E4VhyBLZpoPstw7Xv0I3iQdCx11lziBVX3CR07xF9yCqxmFxsH54Z5MV9GUGw");

        System.out.println(decodedJWT.getClaim("role"));
    }
}