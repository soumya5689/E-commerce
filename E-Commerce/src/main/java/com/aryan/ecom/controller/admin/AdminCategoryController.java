package com.aryan.ecom.controller.admin;

import com.aryan.ecom.dto.CategoryDto;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.services.admin.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Received request to create category with name: {}", categoryDto.getName());
        Category category = categoryService.createCategory(categoryDto);
        log.info("Category created with ID: {}", category.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategory() {
        log.info("Received request to get all categories");
        List<Category> categories = categoryService.getAllCategory();
        log.info("Returning {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

}
