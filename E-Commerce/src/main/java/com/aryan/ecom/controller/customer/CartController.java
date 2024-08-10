package com.aryan.ecom.controller.customer;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.dto.PlaceOrderDto;
import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.services.customer.cart.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
public class CartController {
	private final CartService cartService;

	@PostMapping("/cart")
	public ResponseEntity<?> addProductToCart(@RequestBody AddProductInCartDto addProductInCartDto) {
		log.info("Received request to add product to cart for user with ID: {}", addProductInCartDto.getUserId());
		return cartService.addProductToCart(addProductInCartDto);
	}

	@GetMapping("/cart/{userId}")
	public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
		log.info("Received request to get cart for user with ID: {}", userId);
		OrderDto orderDto = cartService.getCartByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(orderDto);
	}

	@GetMapping("/coupon/{userId}/{code}")
	public ResponseEntity<?> applyCoupon(@PathVariable Long userId, @PathVariable String code) {
		log.info("Received request to apply coupon '{}' for user with ID: {}", code, userId);
		try {
			OrderDto orderDto = cartService.applyCoupon(userId, code);
			return ResponseEntity.ok(orderDto);
		} catch (ValidationException e) {
			log.warn("Failed to apply coupon '{}' for user with ID: {}", code, userId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/addition")
	public ResponseEntity<OrderDto> increaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
		log.info("Received request to increase quantity of product '{}' for user with ID: {}", addProductInCartDto.getProductId(), addProductInCartDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.increaseProductQuantity(addProductInCartDto));
	}

	@PostMapping("/deduction")
	public ResponseEntity<OrderDto> decreaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
		log.info("Received request to decrease quantity of product '{}' for user with ID: {}", addProductInCartDto.getProductId(), addProductInCartDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.decreaseProductQuantity(addProductInCartDto));
	}

	@PostMapping("/placedOrder")
	public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderDto placeOrderDto) {
		log.info("Received request to place order for user with ID: {}", placeOrderDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.placedOrder(placeOrderDto));
	}

	@GetMapping("/myOrders/{userId}")
	public ResponseEntity<List<OrderDto>> getMyPlacedOrders(@PathVariable Long userId){
		log.info("Received request to get placed orders for user with ID: {}", userId);
		return ResponseEntity.ok(cartService.getMyPlacedOrders(userId));
	}

}