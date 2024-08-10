package com.aryan.ecom.controller.admin;

import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.services.admin.category.CategoryService;
import com.aryan.ecom.services.admin.coupon.AdminCouponService;
import com.aryan.ecom.services.jwt.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCouponController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class AdminCouponControllerTest {

    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminCouponService adminCouponService;

    private Coupon coupon;
    private Coupon createdCoupon;

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder().code("FLAT15").build();
        createdCoupon = Coupon.builder().id(1L).code("FLAT15").build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createCoupon() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJSON = objectWriter.writeValueAsString(coupon);
        log.info(requestJSON);

        when(adminCouponService.createCoupon(any(Coupon.class))).thenReturn(createdCoupon);

        mockMvc.perform(post("/api/admin/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getAllCoupon() throws Exception {
        when(adminCouponService.getAllCoupon()).thenReturn(List.of(createdCoupon));
        mockMvc.perform(get("/api/admin/coupons")).andExpect(status().isOk());
    }
}