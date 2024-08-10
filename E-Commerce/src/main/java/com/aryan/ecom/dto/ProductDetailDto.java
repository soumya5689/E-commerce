package com.aryan.ecom.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductDetailDto {
	private ProductDto productDto;
	
	private List<ReviewDto> reviewDtoList;
	
	private List<FAQDto> faqDtoList;
}
