package com.aryan.ecom.controller.admin;

import com.aryan.ecom.dto.AnalyticsResponse;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.services.admin.adminOrder.AdminOrderService;
import com.aryan.ecom.services.jwt.UserDetailsServiceImpl;
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

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class AdminOrderControllerTest {

    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AdminOrderService adminOrderService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllPlacedOrders() throws Exception {
        when(adminOrderService.getAllPlacedOrders()).thenReturn(new ArrayList<>());
        // Act & Assert
        mockMvc.perform(get("/api/admin/placedOrders"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

    }

    @Test
    void changeOrderStatus() throws Exception {
        // Arrange
        Long orderId = 1L;
        String status = "Shipped";
        when(adminOrderService.changeOrderStatus(orderId, status)).thenReturn(OrderDto.builder().id(orderId).orderStatus(OrderStatus.Shipped).build());

        // Act & Assert
        mockMvc.perform(put("/api/admin/order/" + orderId + "/" + status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.orderStatus").value(status));
    }

    @Test
    void getAnalytics() throws Exception {
        // Arrange
        AnalyticsResponse response = new AnalyticsResponse(); // Assuming some fields here
        when(adminOrderService.calculateAnalytics()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/admin/order/analytics"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

    }
}