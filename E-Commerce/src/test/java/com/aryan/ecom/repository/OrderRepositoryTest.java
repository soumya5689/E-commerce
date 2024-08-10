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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
@DirtiesContext
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CouponRepository couponRepository;

    private User user;
    private Category category;
    private Product product;
    private Coupon coupon;
    private Order order;
    private CartItems cartItem;

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

        cartItem = CartItems.builder()
                .product(product)
                .user(user)
                .quantity(2L)
                .build();
        cartItem = cartItemsRepository.save(cartItem);

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
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    public void clearDatabase() {
        cartItemsRepository.deleteAll();
        orderRepository.deleteAll();
        couponRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByUserIdAndOrderStatus() {
        Order foundOrder = orderRepository.findByUserIdAndOrderStatus(user.getId(), OrderStatus.Delivered);
        assertEquals(order, foundOrder);
    }

    @Test
    void findAllByOrderStatusIn() {
        List<Order> orders = orderRepository.findAllByOrderStatusIn(List.of(OrderStatus.Delivered));
        assertTrue(orders.contains(order));
    }

    @Test
    void findByUserIdAndOrderStatusIn() {
        List<Order> orders = orderRepository.findByUserIdAndOrderStatusIn(user.getId(), List.of(OrderStatus.Delivered));
        assertTrue(orders.contains(order));
    }

    @Test
    void findByTrackingId() {
        Optional<Order> optionalOrder = orderRepository.findByTrackingId(order.getTrackingId());
        assertTrue(optionalOrder.isPresent());
        assertEquals(order, optionalOrder.get());
    }

    @Test
    void findByDateBetweenAndOrderStatus() {
        Date startOfMonth = new Date();
        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(new Date(startOfMonth.getTime()-(3600*24)), new Date(startOfMonth.getTime() + (3600 * 24 * 30)), OrderStatus.Delivered);
        assertTrue(orders.contains(order));
    }

    @Test
    void findByDateBetweenAndOrderStatus_OrderNotInDateRange() {
        Date startOfMonth = new Date();
        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(new Date(startOfMonth.getTime()+(3600*24)), new Date(startOfMonth.getTime() + (3600 * 24 * 30)), OrderStatus.Delivered);
        assertFalse(orders.contains(order));
    }

    @Test
    void countByOrderStatus() {
        assertEquals(1, orderRepository.countByOrderStatus(OrderStatus.Delivered));
        assertEquals(0, orderRepository.countByOrderStatus(OrderStatus.Pending));
    }
}
