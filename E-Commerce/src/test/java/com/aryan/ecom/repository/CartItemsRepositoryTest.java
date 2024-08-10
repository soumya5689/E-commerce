package com.aryan.ecom.repository;

import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.*;
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
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Slf4j
class CartItemsRepositoryTest {

    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CouponRepository couponRepository;

    private CartItems cartItem;
    private User user;
    private Category category;
    private Product product;
    private Order order;
    private Coupon coupon;

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

        coupon = Coupon.builder()
                .name("demoName")
                .code("FLAT50")
                .discount(50L)
                .expirationDate(new Date())
                .build();
        coupon = couponRepository.save(coupon);

        order = Order.builder()
                .orderDescription("demoDescription")
                .date(new Date())
                .amount(100L)
                .address("demoAddress")
                .payment("Done")
                .orderStatus(OrderStatus.Delivered)
                .totalAmount(500L)
                .discount(50L)
                .trackingId(UUID.randomUUID())
                .user(user)
                .coupon(coupon)
                .build();
        order = orderRepository.save(order);

        cartItem = CartItems.builder()
                .product(product)
                .order(order)
                .user(user)
                .quantity(2L)
                .build();
        cartItem = cartItemsRepository.save(cartItem);
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    public void clearDatabase() {
        cartItemsRepository.deleteAll();
        orderRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void findByProductIdAndOrderIdAndUserId() {
        Optional<CartItems> foundCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(product.getId(), order.getId(), user.getId());
        assertTrue(foundCartItem.isPresent());
        assertEquals(cartItem, foundCartItem.get());
    }
}