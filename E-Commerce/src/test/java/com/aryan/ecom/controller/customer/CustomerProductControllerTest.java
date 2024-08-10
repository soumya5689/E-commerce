package com.aryan.ecom.controller.customer;

import com.aryan.ecom.dto.ProductDetailDto;
import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.services.customer.CustomerProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CustomerProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;


    @MockBean
    CustomerProductService customerProductService;

    @Test
    void getAllProduct() throws Exception {
        ProductDto product1 = ProductDto.builder().id(1L).name("Product 1").price(100L).build();
        ProductDto product2 = ProductDto.builder().id(2L).name("Product 2").price(200L).build();
        when(customerProductService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/customer/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].price").value(200));
    }

    @Test
    void getAllProductByName() throws Exception {
        ProductDto product = ProductDto.builder().id(1L).name("Product 1").price(100L).build();
        when(customerProductService.getAllProductsByName(anyString())).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/api/customer/search/{name}", "Product"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].price").value(100));
    }

    @Test
    void getProductDetailById() throws Exception {
        ProductDto product = ProductDto.builder().id(1L).name("Product 1").price(100L).build();
        ProductDetailDto productDetailDto = ProductDetailDto.builder().productDto(product).build();
        when(customerProductService.getProductDetailById(anyLong())).thenReturn(productDetailDto);

        mockMvc.perform(get("/api/customer/product/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productDto.id").value(1))
                .andExpect(jsonPath("$.productDto.name").value("Product 1"))
                .andExpect(jsonPath("$.productDto.price").value(100));
    }
}
