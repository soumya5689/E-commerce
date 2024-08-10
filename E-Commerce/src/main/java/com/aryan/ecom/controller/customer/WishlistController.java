package com.aryan.ecom.controller.customer;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.WishlistDto;
import com.aryan.ecom.services.customer.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
@Slf4j
public class WishlistController {
	private final WishlistService wishlistService;

	@PostMapping("/wishlist")
	public ResponseEntity<?> addProductToWishlist(@RequestBody WishlistDto wishlistDto) {
		log.info("Received request to add product to wishlist for user with ID: {}", wishlistDto.getUserId());
		WishlistDto postedWishlistDto = wishlistService.addProductToWishlist(wishlistDto);
		if (postedWishlistDto == null) {
			log.warn("Failed to add product to wishlist for user with ID: {}", wishlistDto.getUserId());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Something went wrong");
		}
		log.info("Product added to wishlist successfully for user with ID: {}", wishlistDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(postedWishlistDto);
	}

	@GetMapping("/wishlist/{userId}")
	public ResponseEntity<List<WishlistDto>> getWishlistByUserId(@PathVariable Long userId) {
		log.info("Received request to get wishlist for user with ID: {}", userId);
		List<WishlistDto> wishlistDtos = wishlistService.getWishlistByUserId(userId);
		return ResponseEntity.ok(wishlistDtos);
	}
}
