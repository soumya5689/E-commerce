package com.aryan.ecom.services.auth;

import com.aryan.ecom.dto.SignupRequest;
import com.aryan.ecom.dto.UserDto;
import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.Order;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.OrderRepository;
import com.aryan.ecom.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    UserRepository userRepository;
    @Mock
    OrderRepository orderRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private AuthService authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, bCryptPasswordEncoder, orderRepository);

        signupRequest = SignupRequest.builder()
                .email("demo@gmail.com")
                .name("demo")
                .password("demo_pass")
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createUser() {
        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("encoded_password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().build());

        UserDto userDto = authService.createUser(signupRequest);

        assertNotNull(userDto.getId());
    }

    @Test
    void hasUserWithEmail() {
        when(userRepository.findFirstByEmail(signupRequest.getEmail())).thenReturn(Optional.of(User.builder().build()));
        assertTrue(authService.hasUserWithEmail("demo@gmail.com"));
        assertFalse(authService.hasUserWithEmail("incorrectMail@gmail.com"));
    }

    @Test
    void createAdminAccount() {
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode("admin")).thenReturn("encoded_admin_password");

        authService.createAdminAccount();

        verify(userRepository).findByRole(UserRole.ADMIN);
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("admin@gmail.com") &&
                        user.getName().equals("admin") &&
                        user.getRole() == UserRole.ADMIN &&
                        user.getPassword().equals("encoded_admin_password")
        ));
    }
}