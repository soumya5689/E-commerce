package com.aryan.ecom.services.customer.cart;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.CartItemsDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.dto.PlaceOrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.model.CartItems;
import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.model.Order;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.CartItemsRepository;
import com.aryan.ecom.repository.CouponRepository;
import com.aryan.ecom.repository.OrderRepository;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

	@Autowired
	private final OrderRepository orderRepository;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final CartItemsRepository cartItemsRepository;

	@Autowired
	private final ProductRepository productRepository;

	@Autowired
	private final CouponRepository couponRepository;

	public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {

		System.out.println(addProductInCartDto.toString());

		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
				OrderStatus.Pending);

		if (activeOrder == null) {
			log.info("active order not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order is empty");

		}

		// check if that same product is already present for that particular user
		Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());

		System.out.println(addProductInCartDto.getUserId() + " <=> " + OrderStatus.Pending);

		if (optionalCartItems.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		} else {
			Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
			Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());
			if (optionalUser.isPresent() && optionalProduct.isPresent()) {
				CartItems cartItems = new CartItems();
				cartItems.setProduct(optionalProduct.get());
				cartItems.setPrice(optionalProduct.get().getPrice());
				cartItems.setQuantity(1L);
				cartItems.setUser(optionalUser.get());
				cartItems.setOrder(activeOrder);

				CartItems updatedCart = cartItemsRepository.save(cartItems);

				activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cartItems.getPrice());
				activeOrder.setAmount(activeOrder.getAmount() + cartItems.getPrice());
				activeOrder.getCartItems().add(updatedCart);

				orderRepository.save(activeOrder);

				return ResponseEntity.status(HttpStatus.CREATED).body(cartItems);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
			}

		}

	}

	public OrderDto getCartByUserId(Long userId) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
		// if order is in other state than pending
		if (activeOrder == null)
			return null;
		List<CartItemsDto> cartItemsDtosList = activeOrder.getCartItems().stream().map(CartItems::getCartDto)
				.collect(Collectors.toList());

		OrderDto orderDto = new OrderDto();
		orderDto.setId(activeOrder.getId());
		orderDto.setAmount(activeOrder.getAmount());
		orderDto.setOrderStatus(activeOrder.getOrderStatus());
		orderDto.setDiscount(activeOrder.getDiscount());
		orderDto.setTotalAmount(activeOrder.getTotalAmount());
		orderDto.setCartItems(cartItemsDtosList);
		if (activeOrder.getCoupon() != null)
			orderDto.setCouponCode(activeOrder.getCoupon().getCode());

		if (activeOrder.getCoupon() != null) {
			orderDto.setCouponName(activeOrder.getCoupon().getName());
		}

		return orderDto;
	}

	public OrderDto applyCoupon(Long userId, String code) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
		Coupon coupon = couponRepository.findByCode(code)
				.orElseThrow(() -> new ValidationException("coupon not found"));
		if (couponIsExpired(coupon)) {
			throw new ValidationException("coupon is expired");
		}
		double discountAmount = ((coupon.getDiscount() / 100.0) * activeOrder.getTotalAmount());
		double netAmount = activeOrder.getTotalAmount() - discountAmount;

		activeOrder.setAmount((long) netAmount);
		activeOrder.setDiscount((long) discountAmount);
		activeOrder.setCoupon(coupon);

		orderRepository.save(activeOrder);
		return activeOrder.getOrderDto();

	}

	public boolean couponIsExpired(Coupon coupon) {
		Date currentDate = new Date();
		Date expirationDate = coupon.getExpirationDate();
		return expirationDate != null && currentDate.after(expirationDate);
	}

	public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
				OrderStatus.Pending);
		Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
		Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				optionalProduct.get().getId(), activeOrder.getId(), addProductInCartDto.getUserId());

		if (optionalCartItem.isPresent()) {
			CartItems cartItems = optionalCartItem.get();
			Product product = optionalProduct.get();

			activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
			activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());

			cartItems.setQuantity(cartItems.getQuantity() + 1);

			if (activeOrder.getCoupon() != null) {
				double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0)
						* activeOrder.getTotalAmount());
				double netAmount = activeOrder.getTotalAmount() - discountAmount;

				activeOrder.setAmount((long) netAmount);
				activeOrder.setDiscount((long) discountAmount);
			}

			cartItemsRepository.save(cartItems);
			orderRepository.save(activeOrder);
			return activeOrder.getOrderDto();
		}
		return null;
	}

	public OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
				OrderStatus.Pending);
		Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
		Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				optionalProduct.get().getId(), activeOrder.getId(), addProductInCartDto.getUserId());

		if (optionalCartItem.isPresent()) {
			CartItems cartItems = optionalCartItem.get();
			Product product = optionalProduct.get();

			// update amount
			activeOrder.setAmount(activeOrder.getAmount() - product.getPrice());
			activeOrder.setTotalAmount(activeOrder.getTotalAmount() - product.getPrice());

			// decrease quantity
			cartItems.setQuantity(cartItems.getQuantity() - 1);

			if (activeOrder.getCoupon() != null) {
				double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0)
						* activeOrder.getTotalAmount());
				double netAmount = activeOrder.getTotalAmount() - discountAmount;

				activeOrder.setAmount((long) netAmount);
				activeOrder.setDiscount((long) discountAmount);
			}

			cartItemsRepository.save(cartItems);
			orderRepository.save(activeOrder);
			return activeOrder.getOrderDto();
		}
		return null;
	}

	public OrderDto placedOrder(PlaceOrderDto placeOrderDto) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(), OrderStatus.Pending);

		if (activeOrder == null) {
			log.info("There is no active pending order for the user with userId: {}", placeOrderDto.getUserId());
			return null;
		}

		Optional<User> optionalUser = userRepository.findById(placeOrderDto.getUserId());
		if (optionalUser.isPresent()) {
			activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());

			// Update the active order with the new details
			activeOrder.setAddress(placeOrderDto.getAddress());
			activeOrder.setDate(new Date());
			activeOrder.setOrderStatus(OrderStatus.Placed);
			activeOrder.setTrackingId(UUID.randomUUID());

			// Save the updated order
			orderRepository.save(activeOrder);
			log.info("Updated order saved with ID: {}", activeOrder.getId());


			// Create a new empty pending order for the user
			Order newOrder = new Order();
			newOrder.setAmount(0L);
			newOrder.setTotalAmount(0L);
			newOrder.setDiscount(0L);
			newOrder.setUser(optionalUser.get());
			newOrder.setOrderStatus(OrderStatus.Pending);

			// Save the new pending order
			orderRepository.save(newOrder);
			log.info("new pending order created : {}",newOrder.toString());

			// Return the DTO of the active order
			return activeOrder.getOrderDto();
		}
		return null;
	}

	public List<OrderDto> getMyPlacedOrders(Long userId) {
		return orderRepository
				.findByUserIdAndOrderStatusIn(userId,
						List.of(OrderStatus.Shipped, OrderStatus.Placed, OrderStatus.Delivered))
				.stream().map(Order::getOrderDto).collect(Collectors.toList());
	}

	public OrderDto searchOrderByTrackingId(UUID trackingId) {
		Optional<Order> optionalOrder = orderRepository.findByTrackingId(trackingId);
        return optionalOrder.map(Order::getOrderDto).orElse(null);
    }

}
