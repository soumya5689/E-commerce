package com.aryan.ecom.services.admin.category;

import java.util.List;

import com.aryan.ecom.dto.CategoryDto;
import com.aryan.ecom.model.Category;

public interface CategoryService {
	 Category createCategory(CategoryDto categoryDto);
	 List<Category> getAllCategory();
}
