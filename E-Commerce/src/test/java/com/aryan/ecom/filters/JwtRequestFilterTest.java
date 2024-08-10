package com.aryan.ecom.filters;

import com.aryan.ecom.services.jwt.UserDetailsServiceImpl;
import com.aryan.ecom.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtRequestFilterTest {

    AutoCloseable autoCloseable;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    FilterChain filterChain;

    @Mock
    UserDetailsServiceImpl userDetailsService;

    @Mock
    UserDetails userDetails;


    @InjectMocks
    private JwtRequestFilter filter;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void doFilterInternal_ValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid-token";
        String username = "testUser";
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

        // Act
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // Assert
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(jwtUtil, times(1)).validateToken(token, userDetails);
    }


    @Test
    void doFilterInternal_InvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(null);

        // Act
        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // Assert
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).validateToken(anyString(), any());
    }


}