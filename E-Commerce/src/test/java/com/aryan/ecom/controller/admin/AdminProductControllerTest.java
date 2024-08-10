package com.aryan.ecom.controller.admin;

import com.aryan.ecom.dto.FAQDto;
import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.services.admin.adminproduct.AdminProductService;
import com.aryan.ecom.services.admin.faq.FAQService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class AdminProductControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AdminProductService adminProductService;
    @MockBean
    FAQService faqService;
    ObjectMapper objectMapper;
    ProductDto productDto;
    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        productDto = ProductDto.builder()
                .name("demoName")
                .price(500L)
                .categoryName("demoCategory")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addProduct() throws Exception {
        when(adminProductService.addProduct(any(ProductDto.class))).thenReturn(ProductDto.builder()
                .id(1L)
                .name("demoName")
                .price(500L)
                .categoryName("demoCategory")
                .build());

        mockMvc.perform(post("/api/admin/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(productDto)))
                .andDo(print()).andExpect(status().isCreated());
    }


    @Test
    void getAllProduct() throws Exception {
        List<ProductDto> productList = Collections.singletonList(productDto);

        when(adminProductService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("demoName"))
                .andExpect(jsonPath("$[0].price").value(500L))
                .andExpect(jsonPath("$[0].categoryName").value("demoCategory"));
    }

    @Test
    void getAllProductByName() throws Exception {
        List<ProductDto> productList = Collections.singletonList(productDto);

        when(adminProductService.getAllProductsByName("demoName")).thenReturn(productList);

        mockMvc.perform(get("/api/admin/search/demoName"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("demoName"))
                .andExpect(jsonPath("$[0].price").value(500L))
                .andExpect(jsonPath("$[0].categoryName").value("demoCategory"));
    }

    @Test
    void deleteProduct() throws Exception {
        when(adminProductService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/product/1"))
                .andExpect(status().isNoContent());

        when(adminProductService.deleteProduct(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/product/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postFAQ() throws Exception {
        FAQDto faqDto = FAQDto.builder()
                .question("What is this?")
                .answer("This is a test product.")
                .build();

        when(faqService.postFAQ(eq(1L), any(FAQDto.class))).thenReturn(faqDto);

        mockMvc.perform(post("/api/admin/faq/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faqDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.question").value("What is this?"))
                .andExpect(jsonPath("$.answer").value("This is a test product."));
    }

    @Test
    void getProductById() throws Exception {
        when(adminProductService.getProductById(1L)).thenReturn(productDto);

        mockMvc.perform(get("/api/admin/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("demoName"))
                .andExpect(jsonPath("$.price").value(500L))
                .andExpect(jsonPath("$.categoryName").value("demoCategory"));

        when(adminProductService.getProductById(2L)).thenReturn(null);

        mockMvc.perform(get("/api/admin/product/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct() throws Exception {
        when(adminProductService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(ProductDto.builder().name("updatedName").price(1000L).categoryName("updatedCategory").build());

        mockMvc.perform(put("/api/admin/product/1")
                        .param("name", "updatedName")
                        .param("price", "1000")
                        .param("categoryName", "updatedCategory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.price").value(1000L))
                .andExpect(jsonPath("$.categoryName").value("updatedCategory"));

        when(adminProductService.updateProduct(eq(2L), any(ProductDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/admin/product/2")
                        .param("name", "updatedName")
                        .param("price", "1000")
                        .param("categoryName", "updatedCategory"))
                .andExpect(status().isNotFound());
    }
}