package com.aryan.ecom.services.customer;

import com.aryan.ecom.dto.ProductDetailDto;
import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.model.*;
import com.aryan.ecom.repository.FAQRepository;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
class CustomerProductServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    ProductRepository productRepository;
    @Mock
    FAQRepository faqRepository;
    @Mock
    ReviewRepository reviewRepository;

    private CustomerProductServiceImpl customerProductService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customerProductService = new CustomerProductServiceImpl(productRepository,faqRepository,reviewRepository);

    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    private Product createMockProduct(Long id){
        return Product.builder()
                .id(id)
                .price(100L)
                .category(Category.builder().id(1L).build())
                .name("demo")
                .build();
    }

    private FAQ createMockFAQ(Long id, Long productId) {
        return FAQ.builder()
                .id(id)
                .product(createMockProduct(productId))
                .question("Sample Question?")
                .answer("Sample Answer")
                .build();
    }

    private Review createMockReview(Long id, Long productId) {
        return Review.builder()
                .id(id)
                .product(createMockProduct(productId))
                .rating(5L)
                .user(User.builder().id(1L).name("User").build())
                .description("Excellent product!")
                .build();
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(createMockProduct(1L),createMockProduct(2L)));
        assertEquals(2,customerProductService.getAllProducts().size());
    }

    @Test
    void getAllProductsByName() {
        String productName = "demo";
        when(productRepository.findAllByNameContaining(productName)).thenReturn(List.of(createMockProduct(1L), createMockProduct(2L)));
        List<ProductDto> productDtos = customerProductService.getAllProductsByName(productName);
        assertNotNull(productDtos);
        assertEquals(2, productDtos.size());

    }

    @Test
    void getProductDetailById() {
        Long productId = 1L;
        Product mockProduct = createMockProduct(productId);
        List<FAQ> faqs = List.of(createMockFAQ(1L, productId));
        List<Review> reviews = List.of(createMockReview(1L, productId));

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(faqRepository.findAllByProductId(productId)).thenReturn(faqs);
        when(reviewRepository.findAllByProductId(productId)).thenReturn(reviews);

        ProductDetailDto productDetailDto = customerProductService.getProductDetailById(productId);

        assertNotNull(productDetailDto);
        assertNotNull(productDetailDto.getProductDto());
        assertEquals(1, productDetailDto.getFaqDtoList().size());
        assertEquals(1, productDetailDto.getReviewDtoList().size());
    }
}