package com.aryan.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WishlistDto {
	private Long userId;
	
	private Long productId;
	
	private Long id;
	
	private String productName;
	
	private String productDescription;
	
	private byte[] returnedImg;
	
	private Long price;
	
}
