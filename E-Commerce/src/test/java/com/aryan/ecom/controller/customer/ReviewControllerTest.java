package com.aryan.ecom.controller.customer;

import com.aryan.ecom.controller.admin.AdminProductController;
import com.aryan.ecom.dto.OrderedProductsResponseDto;
import com.aryan.ecom.dto.ReviewDto;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.services.customer.review.ReviewService;
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

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    ReviewService reviewService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getOrderedProductDetailsByOrderId() throws Exception {

        OrderedProductsResponseDto responseDto = new OrderedProductsResponseDto();
        when(reviewService.getOrderedProductsDetailsByOrderId(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/customer/ordered-products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void giveReview() throws Exception {
        ReviewDto reviewDto = new ReviewDto();
        when(reviewService.giveReview(reviewDto)).thenReturn(reviewDto);

        mockMvc.perform(post("/api/customer/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reviewDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").doesNotExist());
    }
}