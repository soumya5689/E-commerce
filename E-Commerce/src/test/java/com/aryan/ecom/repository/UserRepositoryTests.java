package com.aryan.ecom.repository;

import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().email("demoEmail@mail.com").name("demoName").password(new BCryptPasswordEncoder().encode("demoPassword")).role(UserRole.CUSTOMER).build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        user = null;
        userRepository.deleteAll();
    }

    @Test
    void testFindFirstByEmail_Found() {
        Optional<User> foundUser = userRepository.findFirstByEmail("demoEmail@mail.com");
        assertEquals(foundUser.get().getEmail(), user.getEmail());
        assertEquals(foundUser.get().getPassword(), user.getPassword());
    }

    @Test
    void testFindFirstByEmail_NotFound() {
        Optional<User> foundUser = userRepository.findFirstByEmail("incorrectMail@mail.com");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findByRole_Found() {
        Optional<User> foundUser = userRepository.findByRole(UserRole.CUSTOMER);
        assertEquals(foundUser.get().getRole(), UserRole.CUSTOMER);
    }

    @Test
    void findByRole_NotFound() {
        Optional<User> foundUser = userRepository.findByRole(UserRole.ADMIN);
        assertTrue(foundUser.isEmpty());
    }

}
