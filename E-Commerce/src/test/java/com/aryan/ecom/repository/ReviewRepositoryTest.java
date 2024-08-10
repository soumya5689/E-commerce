package com.aryan.ecom.repository;

import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.Review;
import com.aryan.ecom.model.User;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    private Review review;
    private User user;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() throws IOException {
        clearDatabase();

        user = User.builder()
                .email("demoEmail@mail.com")
                .name("demoName")
                .password(new BCryptPasswordEncoder().encode("demoPassword"))
                .role(UserRole.CUSTOMER)
                .build();
        user = userRepository.save(user);
        log.info("User Created : {}", user.toString());


        category = Category.builder()
                .name("demoCategory")
                .description("demoDescription")
                .build();
        category = categoryRepository.save(category);
        log.info("Category Created : {}", category.toString());


        MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());
        product = Product.builder()
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("demoDescription")
                .build();
        product = productRepository.save(product);
        log.info("Product Created : {}", product.toString());

        // Adding 2 reviews for same product
        review = Review.builder()
                .rating(4L)
                .description("demoDescription")
                .img(mockMultipartFile.getBytes())
                .user(user)
                .product(product)
                .build();
        review = reviewRepository.save(review);
        log.info("Review Created : {}", review.toString());


        review = Review.builder()
                .rating(5L)
                .description("demoDescription")
                .img(mockMultipartFile.getBytes())
                .user(user)
                .product(product)
                .build();
        review = reviewRepository.save(review);
        log.info("Review Created : {}", review.toString());

    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    public void clearDatabase() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findAllByProductId() {
        List<Review> reviewList = reviewRepository.findAllByProductId(product.getId());
        assertEquals(2, reviewList.size());
        for (Review r : reviewList) {
            assertEquals(product.getId(), r.getProduct().getId());
            assertEquals(user.getId(), r.getUser().getId());
            assertNotNull(r.getDescription());
            assertNotNull(r.getImg());
            assertTrue(r.getRating() >= 1 && r.getRating() <= 5);
        }
    }
}