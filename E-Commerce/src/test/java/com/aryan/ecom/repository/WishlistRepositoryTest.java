package com.aryan.ecom.repository;

import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.User;
import com.aryan.ecom.model.Wishlist;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Slf4j
class WishlistRepositoryTest {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private User user;
    private Wishlist wishlist;

    @BeforeEach
    void setUp() throws IOException {
        user = User.builder()
                .email("demoEmail@mail.com")
                .name("demoName")
                .password(new BCryptPasswordEncoder().encode("demoPassword"))
                .role(UserRole.CUSTOMER)
                .build();
        user = userRepository.save(user);
        log.info("User Created : {}", user.toString());

        Category category = Category.builder()
                .name("demoCategory")
                .description("demoDescription")
                .build();
        category = categoryRepository.save(category);
        log.info("Category Created : {}", category.toString());

        MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());
        Product product = Product.builder()
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("demoDescription")
                .build();
        product = productRepository.save(product);
        log.info("Product Created : {}", product.toString());

        wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        wishlist = wishlistRepository.save(wishlist);
    }

    @AfterEach
    void tearDown() {
        wishlistRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void findAllByUserId() {
        List<Wishlist> wishlists = wishlistRepository.findAllByUserId(user.getId());
        assertNotNull(wishlists);
        assertTrue(wishlists.contains(wishlist));
    }
}