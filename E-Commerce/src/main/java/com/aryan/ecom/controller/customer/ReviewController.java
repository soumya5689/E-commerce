package com.aryan.ecom.controller.customer;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.OrderedProductsResponseDto;
import com.aryan.ecom.dto.ReviewDto;
import com.aryan.ecom.services.customer.review.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
@Slf4j
public class ReviewController {
	private final ReviewService reviewService;

	@GetMapping("/ordered-products/{orderId}")
	public ResponseEntity<OrderedProductsResponseDto> getOrderedProductDetailsByOrderId(@PathVariable Long orderId) {
		log.info("Received request to get ordered product details for order with ID: {}", orderId);
		OrderedProductsResponseDto responseDto = reviewService.getOrderedProductsDetailsByOrderId(orderId);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/review")
	public ResponseEntity<?> giveReview(@ModelAttribute ReviewDto reviewDto) throws IOException {
		log.info("Received request to submit review for product with ID: {}", reviewDto.getProductId());
		ReviewDto submittedReview = reviewService.giveReview(reviewDto);
		if (submittedReview == null) {
			log.warn("Failed to submit review for product with ID: {}", reviewDto.getProductId());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
		}
		log.info("Review submitted successfully for product with ID: {}", reviewDto.getProductId());
		return ResponseEntity.status(HttpStatus.CREATED).body(submittedReview);
	}
}
