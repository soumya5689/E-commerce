package com.aryan.ecom.controller.customer;

import com.aryan.ecom.dto.WishlistDto;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.services.customer.wishlist.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class WishlistControllerTest {

    @Autowired
    MockMvc mockMvc;

    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    WishlistService wishlistService;

    ObjectMapper objectMapper;
    WishlistDto wishlistDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
         wishlistDto = WishlistDto.builder()
                .userId(1L)
                .productId(1L)
                .productName("Product Name")
                .productDescription("Product Description")
                .price(100L)
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addProductToWishlist() throws Exception {
        when(wishlistService.addProductToWishlist(any(WishlistDto.class))).thenReturn(wishlistDto);

        mockMvc.perform(post("/api/customer/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.productName").value("Product Name"))
                .andExpect(jsonPath("$.productDescription").value("Product Description"))
                .andExpect(jsonPath("$.price").value(100L));

        when(wishlistService.addProductToWishlist(any(WishlistDto.class))).thenReturn(null);

        mockMvc.perform(post("/api/customer/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistDto)))
                .andExpect(status().isBadGateway())
                .andExpect(content().string("Something went wrong"));
    }

    @Test
    void getWishlistByUserId() throws Exception {
        List<WishlistDto> wishlist = Collections.singletonList(wishlistDto);

        when(wishlistService.getWishlistByUserId(eq(1L))).thenReturn(wishlist);

        mockMvc.perform(get("/api/customer/wishlist/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].productName").value("Product Name"))
                .andExpect(jsonPath("$[0].productDescription").value("Product Description"))
                .andExpect(jsonPath("$[0].price").value(100L));
    }
}