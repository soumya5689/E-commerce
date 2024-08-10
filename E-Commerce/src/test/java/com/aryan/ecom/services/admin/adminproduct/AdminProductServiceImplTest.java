package com.aryan.ecom.services.admin.adminproduct;

import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.repository.CategoryRepository;
import com.aryan.ecom.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminProductServiceImplTest {

    AutoCloseable autoCloseable;
    Product product;
    Product savedProduct;
    Product updatedProduct;
    ProductDto productDto;
    Category category;
    List<Product> products;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    private AdminProductService adminProductService;

//    Used to mock image
    MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());


    @BeforeEach
    void setUp() throws IOException {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adminProductService = new AdminProductServiceImpl(productRepository, categoryRepository);
        category = Category.builder()
                .id(1L)
                .name("demoCategory")
                .description("demoDescription")
                .build();


        product = Product.builder()
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("demoDescription")
                .build();

        savedProduct = Product.builder()
                .id(1L)
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("demoDescription")
                .build();

        updatedProduct = Product.builder()
                .id(1L)
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("updatedDescription")
                .build();


        productDto = ProductDto.builder()
                .name("demoName")
                .description("demoDescription")
                .categoryId(1L)
                .categoryName("demoName")
                .price(200L)
                .img(mockMultipartFile)
                .build();

        // for getProduct tests
        products = new ArrayList<>();
        products.add(product);
        products.add(savedProduct);
        products.add(updatedProduct);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testAddProduct_Added() throws Exception {
        mock(ProductRepository.class);
        mock(CategoryRepository.class);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // for some reason this doesnt work:         when(productRepository.save(product)).thenReturn(product); TODO : understand
        when(productRepository.save(product)).thenReturn(savedProduct);

        assertEquals(adminProductService.addProduct(productDto).getName(), productDto.getName());
        assertEquals(adminProductService.addProduct(productDto).getCategoryId(), productDto.getCategoryId());
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(products);
        assertEquals(updatedProduct.getDescription(), adminProductService.getAllProducts().get(2).getDescription());
    }

    @Test
    void getAllProducts_NoProductFound() {
        mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(adminProductService.getAllProducts().isEmpty());
    }

    @Test
    void getAllProductsByName() {
        when(productRepository.findAllByNameContaining("demo")).thenReturn(products);
        List<ProductDto> foundProducts = adminProductService.getAllProductsByName("demo");
        assertEquals(3, foundProducts.size());

        for (ProductDto product : foundProducts) {
            assertTrue(product.getName().contains("demo"));
            assertFalse(product.getName().contains("other"));
        }

        assertTrue(adminProductService.getAllProductsByName("nonExistentKeyword").isEmpty());
    }

    @Test
    void deleteProduct_Deleted() {
        // mock jpa methods
        when(productRepository.findById(any())).thenReturn(Optional.of(savedProduct));
        doAnswer(Answers.CALLS_REAL_METHODS).when(
          productRepository).deleteById(any());

        assertTrue(adminProductService.deleteProduct(1L));
    }

    @Test
    void deleteProduct_NotFound() {
        mock(ProductRepository.class, CALLS_REAL_METHODS);
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        doAnswer(Answers.CALLS_REAL_METHODS).when(
                productRepository).deleteById(any());

        assertFalse(adminProductService.deleteProduct(1L));
    }

    @Test
    void getProductById_Found() {
        mock(ProductRepository.class, CALLS_REAL_METHODS);
        when(productRepository.findById(any())).thenReturn(Optional.of(savedProduct));
        assertNotNull(adminProductService.getProductById(1L));
        assertEquals(savedProduct.getId(), adminProductService.getProductById(1L).getId());
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        assertNull(adminProductService.getProductById(1L));
    }

    @Test
    void updateProduct() throws IOException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(updatedProduct);

        ProductDto updatedDto = adminProductService.updateProduct(1L, productDto);
        assertNotNull(updatedDto);
        assertEquals(savedProduct.getId(), updatedDto.getId());
        assertEquals(updatedProduct.getDescription(), updatedDto.getDescription());
    }
}