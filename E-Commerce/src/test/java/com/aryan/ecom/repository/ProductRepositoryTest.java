package com.aryan.ecom.repository;

import com.aryan.ecom.model.Category;
import com.aryan.ecom.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Slf4j
class ProductRepositoryTest {
    Category category;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() throws Exception {
        tearDown();
        category = Category.builder()
                .name("demoCategory")
                .description("demoDescription")
                .build();
        categoryRepository.save(category);

        MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());

        for (int i = 0; i < 10; i++) {
            Product product = Product.builder()
                    .name("demoName")
                    .price(200L)
                    .img(mockMultipartFile.getBytes())
                    .category(category)
                    .description("demoDescription")
                    .build();
            if(i<5){
                product.setName("low");
            }else product.setName("high");
            productRepository.save(product);
        }
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void findAllByNameContaining_Found() {
        List<Product> products = productRepository.findAllByNameContaining("low");
        assertEquals(products.size(), 5);
    }

    @Test
    void findAllByNameContaining_NotFound(){
        List<Product> products = productRepository.findAllByNameContaining("incorrect");
        assertTrue(products.isEmpty());
    }

}