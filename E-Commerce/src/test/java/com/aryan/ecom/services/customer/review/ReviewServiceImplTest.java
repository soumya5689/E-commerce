package com.aryan.ecom.services.customer.review;

import com.aryan.ecom.dto.OrderedProductsResponseDto;
import com.aryan.ecom.dto.ReviewDto;
import com.aryan.ecom.model.*;
import com.aryan.ecom.repository.OrderRepository;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.ReviewRepository;
import com.aryan.ecom.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ReviewServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ReviewRepository reviewRepository;

    private ReviewService reviewService;

    private Order order;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        reviewService = new ReviewServiceImpl(orderRepository, productRepository, userRepository, reviewRepository);

        order = Order.builder()
                .id(1L)
                .amount(500L)
                .cartItems(List.of(CartItems.builder()
                                .id(1L)
                                .product(Product.builder().id(1L).build())
                                .build(),
                        CartItems.builder()
                                .id(2L)
                                .product(Product.builder().id(2L).build())
                                .build()
                ))
                .build();

        reviewDto = ReviewDto.builder()
                .rating(4L)
                .userId(1L)
                .img(new MockMultipartFile("img", "image.jpg", "image/jpeg", "imageData".getBytes()))
                .productId(1L)
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getOrderedProductsDetailsByOrderId() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderedProductsResponseDto response = reviewService.getOrderedProductsDetailsByOrderId(1L);
        assertEquals(order.getCartItems().size(), response.getProductDtoList().size());
        assertEquals(order.getAmount(), response.getOrderAmount());
    }

    @Test
    void getOrderedProductsDetailsByOrderId_nonExistentOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        OrderedProductsResponseDto response = reviewService.getOrderedProductsDetailsByOrderId(1L);

        assertNull(response.getOrderAmount());
        assertNull(response.getProductDtoList());
    }

    @Test
    void giveReview_success() throws IOException {
        Product product = Product.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        Review review = Review.builder()
                .id(1L)
                .description("Great product!")
                .rating(4L)
                .user(user)
                .product(product)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDto savedReview = reviewService.giveReview(reviewDto);

        assertEquals(review.getId(), savedReview.getId());
        assertEquals(review.getDescription(), savedReview.getDescription());
        assertEquals(review.getRating(), savedReview.getRating());
    }

    @Test
    void giveReview_productNotFound() throws IOException {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

        ReviewDto savedReview = reviewService.giveReview(reviewDto);

        assertNull(savedReview);
    }

    @Test
    void giveReview_userNotFound() throws IOException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(Product.builder().id(1L).build()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ReviewDto savedReview = reviewService.giveReview(reviewDto);

        assertNull(savedReview);
    }

}