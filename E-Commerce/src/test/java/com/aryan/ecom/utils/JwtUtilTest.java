package com.aryan.ecom.utils;

import ch.qos.logback.core.encoder.EchoEncoder;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@Slf4j
class JwtUtilTest {

    AutoCloseable autoCloseable;

    @Mock
    UserDetails userDetails;

    private JwtUtil jwtUtil;
    private String token;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
        when(userDetails.getUsername()).thenReturn("testUser");
        token = jwtUtil.generateToken("testUser");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void generateToken() {
        assertNotNull(token);
    }

    @Test
    void extractUsername() {
        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void extractClaim() {
        String username = jwtUtil.extractClaim(token, Claims::getSubject);
        assertEquals("testUser", username);
    }

    @Test
    void validateToken() {
        assertTrue(jwtUtil.validateToken(token, userDetails));
        String invalidToken = jwtUtil.generateToken("invalidUser");
        assertFalse(jwtUtil.validateToken(invalidToken, userDetails));
    }
}