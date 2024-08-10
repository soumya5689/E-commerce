package com.aryan.ecom.services.customer;

import java.util.List;

import com.aryan.ecom.dto.ProductDetailDto;
import com.aryan.ecom.dto.ProductDto;

public interface CustomerProductService {
	List<ProductDto> getAllProducts();

	List<ProductDto> getAllProductsByName(String name);
	
	ProductDetailDto getProductDetailById(Long productId);

}
