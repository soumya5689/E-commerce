package com.aryan.ecom.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
@Builder
public class ProductDto {
	private Long id;
	
	private String name;
	
	private Long price;
	
	private String description;

	private byte[] byteImg;
	
	private Long categoryId;
	
	private String categoryName; 
	
	private MultipartFile img;	
	
	private Long quantity;
}
