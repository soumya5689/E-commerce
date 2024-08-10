package com.aryan.ecom.repository;

import com.aryan.ecom.model.Category;
import com.aryan.ecom.model.FAQ;
import com.aryan.ecom.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
@Transactional
class FAQRepositoryTest {
    @Autowired
    private FAQRepository faqRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private FAQ faq;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() throws IOException {
        clearDatabase();

        category = Category.builder()
                .name("demoCategory")
                .description("demoDescription")
                .build();
        category = categoryRepository.save(category);
        log.info("Category Created : {}",category.toString());

        MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());
        product = Product.builder()
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("demoDescription")
                .build();
        product = productRepository.save(product);
        log.info("Product Created : {}",product.toString());

        // Add 2 FAQ for single product
        faq = FAQ.builder()
                .question("Question 1?")
                .answer("Answer !!")
                .product(product)
                .build();
        faq = faqRepository.save(faq);
        log.info("FAQ Created : {}",faq.toString());

        faq = FAQ.builder()
                .question("Question 2?")
                .answer("Answer !!")
                .product(product)
                .build();
        faq = faqRepository.save(faq);
        log.info("FAQ Created : {}",faq.toString());

    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    private void clearDatabase() {
        faqRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void findAllByProductId() {
        List<FAQ> faqList = faqRepository.findAllByProductId(product.getId());
        assertNotNull(faqList);
        assertEquals(2, faqList.size());

        for (FAQ faq : faqList) {
            assertEquals(product.getId(), faq.getProduct().getId());
        }

    }
}