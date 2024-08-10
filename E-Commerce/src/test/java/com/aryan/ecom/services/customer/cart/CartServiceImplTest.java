package com.aryan.ecom.services.customer.cart;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.dto.PlaceOrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.model.*;
import com.aryan.ecom.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
class CartServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    OrderRepository orderRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CartItemsRepository cartItemsRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    CouponRepository couponRepository;

    private CartServiceImpl cartService;

    private Order createMockOrder(Long id) {
        /*
           ``` .cartItems(List.of(CartItems.builder().build())) ```
            context : List.of(...) method, which creates an immutable list.
            This creates an immutable list, which cannot be modified later on. However, in test,
            trying to add an item to this list using Mockito stubbing:

            This will lead to an UnsupportedOperationException because you're trying to modify an immutable list.

            To fix this issue, we should create a mutable list instead of using List.of(...) when creating the Order object in your test.
            We can use ArrayList for this purpose:
            ``` .cartItems(new ArrayList<>(List.of(CartItems.builder().build()))) ```
         */

        return Order.builder()
                .id(id)
                .orderStatus(OrderStatus.Pending)
                .amount(100L)
                .totalAmount(100L)
                .coupon(Coupon.builder().discount(10L).build())
                .cartItems(new ArrayList<>(List.of(CartItems.builder()
                        .product(Product.builder()
                                .id(1L)
                                .build())
                        .user(User.builder()
                                .id(1L)
                                .build())
                        .build())))
                .build();
    }

    private Product createMockProduct(Long id) {
        return Product.builder()
                .id(id)
                .price(100L)
                .build();
    }

    private AddProductInCartDto createMockAddProductInCartDto() {
        AddProductInCartDto addProductInCartDto = new AddProductInCartDto();
        addProductInCartDto.setUserId(1L);
        addProductInCartDto.setProductId(1L);
        return addProductInCartDto;
    }

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl(orderRepository, userRepository, cartItemsRepository, productRepository, couponRepository);
    }


    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addProductToCart() {
        Order mockOrder = createMockOrder(null);
        Product mockProduct = createMockProduct(1L);
        AddProductInCartDto addProductInCartDto = createMockAddProductInCartDto();

        when(orderRepository.findByUserIdAndOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(mockOrder);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mockProduct));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(CartItems.builder().id(1L).build()));
        when(cartItemsRepository.save(any(CartItems.class))).thenReturn(CartItems.builder().id(1L).build());
        when(orderRepository.save(any(Order.class))).thenReturn(createMockOrder(2L));

        assertEquals(100, mockOrder.getAmount());

        ResponseEntity<?> responseEntity = cartService.addProductToCart(addProductInCartDto);

        // Checking the response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void getCartByUserId() {
        when(orderRepository.findByUserIdAndOrderStatus(anyLong(), eq(OrderStatus.Pending))).thenReturn(createMockOrder(1L));
        OrderDto result = cartService.getCartByUserId(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void applyCoupon() {
        when(orderRepository.findByUserIdAndOrderStatus(anyLong(), eq(OrderStatus.Pending))).thenReturn(createMockOrder(1L));
        when(couponRepository.findByCode(any(String.class))).thenReturn(Optional.of(Coupon.builder().id(1L)
                .expirationDate(new Date(new Date().getTime() + (3600)))
                .discount(10L)
                .build()));
        OrderDto orderDto = cartService.applyCoupon(1L, "DEMO");
        assertEquals(orderDto.getDiscount(), 10L);       // 10% of 100

    }

    @Test
    void couponIsExpired_Expired() {
        assertTrue(cartService.couponIsExpired(Coupon.builder().expirationDate(new Date(new Date().getTime() - 1)).build()));
    }

    @Test
    void couponIsExpired_NotExpired() {
        assertFalse(cartService.couponIsExpired(Coupon.builder().expirationDate(new Date(new Date().getTime() + 3600)).build()));
    }

    @Test
    void increaseProductQuantity() {
        when(orderRepository.findByUserIdAndOrderStatus(anyLong(), eq(OrderStatus.Pending))).thenReturn(createMockOrder(1L));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(createMockProduct(1L)));
        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(1L, 1L, 1L)).thenReturn(Optional.of(CartItems.builder().id(1L).quantity(10L).build()));
        when(cartItemsRepository.save(any(CartItems.class))).thenReturn(null);
        when(orderRepository.save(any(Order.class))).thenReturn(null);
        OrderDto orderDto = cartService.increaseProductQuantity(createMockAddProductInCartDto());
        log.info(orderDto.toString());
        assertNotNull(orderDto);
        assertNotEquals(0, orderDto.getDiscount());
        assertEquals(200L, orderDto.getTotalAmount());
        assertEquals(20L, orderDto.getDiscount());   // 10% discount
        assertEquals(180L, orderDto.getAmount());
    }

    @Test
    void decreaseProductQuantity() {
        when(orderRepository.findByUserIdAndOrderStatus(anyLong(), eq(OrderStatus.Pending))).thenReturn(createMockOrder(1L));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(createMockProduct(1L)));
        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(1L, 1L, 1L)).thenReturn(Optional.of(CartItems.builder().id(1L).quantity(10L).build()));
        when(cartItemsRepository.save(any(CartItems.class))).thenReturn(null);
        when(orderRepository.save(any(Order.class))).thenReturn(null);

        OrderDto orderDto = cartService.decreaseProductQuantity(createMockAddProductInCartDto());
        log.info(orderDto.toString());
        assertNotNull(orderDto);

        // all values to 0 as quantity will be Zero
        assertEquals(0L, orderDto.getTotalAmount());
        assertEquals(0L, orderDto.getDiscount());
        assertEquals(0L, orderDto.getAmount());

    }

    @Test
    void placedOrder() {
        when(orderRepository.findByUserIdAndOrderStatus(anyLong(), eq(OrderStatus.Pending))).thenReturn(createMockOrder(1L));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));

        Order order = createMockOrder(1L);
        order.setOrderStatus(OrderStatus.Placed);

        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().user(User.builder().id(1L).build()).build());      // creating empty order

        OrderDto orderDto = cartService.placedOrder(PlaceOrderDto.builder().userId(1L)
                .orderDescription("demodesc")
                .address("addr")
                .build());
        assertEquals(OrderStatus.Placed, orderDto.getOrderStatus());
        log.info(orderDto.toString());
        assertEquals(1L,orderDto.getId());


    }

    @Test
    void getMyPlacedOrders() {
        when(orderRepository.findByUserIdAndOrderStatusIn(anyLong(), eq(List.of(OrderStatus.Shipped, OrderStatus.Placed, OrderStatus.Delivered))))
                .thenReturn(List.of(createMockOrder(1L), createMockOrder(2L)));
        assertEquals(2L, cartService.getMyPlacedOrders(1L).size());
    }

    @Test
    void searchOrderByTrackingId() {
        when(orderRepository.findByTrackingId(any(UUID.class))).thenReturn(Optional.empty());
        assertEquals(null, cartService.searchOrderByTrackingId(UUID.randomUUID()));

        UUID uuid = UUID.randomUUID();
        when(orderRepository.findByTrackingId(uuid)).thenReturn(Optional.of(Order.builder().build()));
        assertNotNull(cartService.searchOrderByTrackingId(uuid));

    }
}