package com.aryan.ecom.controller.customer;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.dto.PlaceOrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.services.customer.cart.CartService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    CartService cartService;

    ObjectMapper objectMapper;
    OrderDto orderDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        orderDto = OrderDto.builder()
                .id(1L)
                .orderDescription("Test Order")
                .date(new Date())
                .amount(100L)
                .address("123 Test Street")
                .payment("Credit Card")
                .orderStatus(OrderStatus.Pending)
                .totalAmount(90L)
                .discount(10L)
                .trackingId(UUID.randomUUID())
                .userName("testuser")
                .cartItems(Collections.emptyList())
                .couponName("Test Coupon")
                .couponCode("TEST10")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addProductToCart() throws Exception {
        AddProductInCartDto addProductInCartDto = new AddProductInCartDto();
        addProductInCartDto.setUserId(1L);
        addProductInCartDto.setProductId(1L);

        when(cartService.addProductToCart(any(AddProductInCartDto.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post("/api/customer/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductInCartDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getCartByUserId() throws Exception {
        when(cartService.getCartByUserId(1L)).thenReturn(orderDto);

        mockMvc.perform(get("/api/customer/cart/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderDescription").value("Test Order"))
                .andExpect(jsonPath("$.amount").value(100L))
                .andExpect(jsonPath("$.address").value("123 Test Street"))
                .andExpect(jsonPath("$.payment").value("Credit Card"))
                .andExpect(jsonPath("$.orderStatus").value("Pending"))
                .andExpect(jsonPath("$.totalAmount").value(90L))
                .andExpect(jsonPath("$.discount").value(10L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.couponName").value("Test Coupon"))
                .andExpect(jsonPath("$.couponCode").value("TEST10"));
    }

    @Test
    void applyCoupon() throws Exception {
        when(cartService.applyCoupon(1L, "TEST10")).thenReturn(orderDto);

        mockMvc.perform(get("/api/customer/coupon/1/TEST10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderDescription").value("Test Order"))
                .andExpect(jsonPath("$.amount").value(100L))
                .andExpect(jsonPath("$.address").value("123 Test Street"))
                .andExpect(jsonPath("$.payment").value("Credit Card"))
                .andExpect(jsonPath("$.orderStatus").value("Pending"))
                .andExpect(jsonPath("$.totalAmount").value(90L))
                .andExpect(jsonPath("$.discount").value(10L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.couponName").value("Test Coupon"))
                .andExpect(jsonPath("$.couponCode").value("TEST10"));

        when(cartService.applyCoupon(1L, "INVALID_CODE")).thenThrow(new ValidationException("Invalid coupon code"));

        mockMvc.perform(get("/api/customer/coupon/1/INVALID_CODE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid coupon code"));
    }

    @Test
    void increaseProductQuantity() throws Exception {
        AddProductInCartDto addProductInCartDto = new AddProductInCartDto();
        addProductInCartDto.setUserId(1L);
        addProductInCartDto.setProductId(1L);

        when(cartService.increaseProductQuantity(any(AddProductInCartDto.class))).thenReturn(orderDto);

        mockMvc.perform(post("/api/customer/addition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductInCartDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderDescription").value("Test Order"))
                .andExpect(jsonPath("$.amount").value(100L))
                .andExpect(jsonPath("$.address").value("123 Test Street"))
                .andExpect(jsonPath("$.payment").value("Credit Card"))
                .andExpect(jsonPath("$.orderStatus").value("Pending"))
                .andExpect(jsonPath("$.totalAmount").value(90L))
                .andExpect(jsonPath("$.discount").value(10L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.couponName").value("Test Coupon"))
                .andExpect(jsonPath("$.couponCode").value("TEST10"));
    }

    @Test
    void decreaseProductQuantity() throws Exception {
        AddProductInCartDto addProductInCartDto = new AddProductInCartDto();
        addProductInCartDto.setUserId(1L);
        addProductInCartDto.setProductId(1L);

        when(cartService.decreaseProductQuantity(any(AddProductInCartDto.class))).thenReturn(orderDto);

        mockMvc.perform(post("/api/customer/deduction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductInCartDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderDescription").value("Test Order"))
                .andExpect(jsonPath("$.amount").value(100L))
                .andExpect(jsonPath("$.address").value("123 Test Street"))
                .andExpect(jsonPath("$.payment").value("Credit Card"))
                .andExpect(jsonPath("$.orderStatus").value("Pending"))
                .andExpect(jsonPath("$.totalAmount").value(90L))
                .andExpect(jsonPath("$.discount").value(10L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.couponName").value("Test Coupon"))
                .andExpect(jsonPath("$.couponCode").value("TEST10"));
    }

    @Test
    void placeOrder() throws Exception {
        PlaceOrderDto placeOrderDto = new PlaceOrderDto();
        placeOrderDto.setUserId(1L);
        placeOrderDto.setAddress(orderDto.getAddress());
        placeOrderDto.setOrderDescription(orderDto.getOrderDescription());

        when(cartService.placedOrder(any(PlaceOrderDto.class))).thenReturn(orderDto);

        mockMvc.perform(post("/api/customer/placedOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeOrderDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderDescription").value("Test Order"))
                .andExpect(jsonPath("$.amount").value(100L))
                .andExpect(jsonPath("$.address").value("123 Test Street"))
                .andExpect(jsonPath("$.payment").value("Credit Card"))
                .andExpect(jsonPath("$.orderStatus").value("Pending"))
                .andExpect(jsonPath("$.totalAmount").value(90L))
                .andExpect(jsonPath("$.discount").value(10L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.couponName").value("Test Coupon"))
                .andExpect(jsonPath("$.couponCode").value("TEST10"));
    }

    @Test
    void getMyPlacedOrders() throws Exception {
        List<OrderDto> orders = Collections.singletonList(orderDto);

        when(cartService.getMyPlacedOrders(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/customer/myOrders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderDescription").value("Test Order"))
                .andExpect(jsonPath("$[0].amount").value(100L))
                .andExpect(jsonPath("$[0].address").value("123 Test Street"))
                .andExpect(jsonPath("$[0].payment").value("Credit Card"))
                .andExpect(jsonPath("$[0].orderStatus").value("Pending"))
                .andExpect(jsonPath("$[0].totalAmount").value(90L))
                .andExpect(jsonPath("$[0].discount").value(10L))
                .andExpect(jsonPath("$[0].userName").value("testuser"))
                .andExpect(jsonPath("$[0].couponName").value("Test Coupon"))
                .andExpect(jsonPath("$[0].couponCode").value("TEST10"));
    }
}
