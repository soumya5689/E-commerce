package com.aryan.ecom.services.jwt;

import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
class UserDetailsServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);

        user = User.builder()
                .id(1L)
                .email("demo@gmail.com")
                .role(UserRole.CUSTOMER)
                .password("demoPass")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void loadUserByUsername_UserFound() {
        when(userRepository.findFirstByEmail("demo@gmail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findFirstByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@gmail.com");
        });

        String expectedMessage = "username not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}