package com.aryan.ecom.controller.admin;

import com.aryan.ecom.dto.CategoryDto;
import com.aryan.ecom.filters.JwtRequestFilter;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.services.admin.category.CategoryService;
import com.aryan.ecom.services.jwt.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(AdminCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class AdminCategoryControllerTest {
    // TO satisfy dependency requirements
    @MockBean
    private JwtRequestFilter jwtRequestFilter;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;

    private Category category1;
    private Category category2;
    private CategoryDto categoryDto;
    List<Category> categories;

    @BeforeEach
    void setUp() {
        category1 = Category.builder()
                .name("cat1")
                .description("cat1_description")
                .build();
        category2 = Category.builder()
                .name("cat2")
                .description("cat2_description")
                .build();
        categoryDto = category1.getDto();
        categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createCategory() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJSON = objectWriter.writeValueAsString(categoryDto);
        log.info(requestJSON);

        when(categoryService.createCategory(categoryDto)).thenReturn(category1);
        log.info(objectWriter.writeValueAsString(category1));

        mockMvc.perform(post("/api/admin/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON))
                .andDo(print()).andExpect(status().isCreated());

    }

    @Test
    void getAllCategory() throws Exception {
        when(categoryService.getAllCategory()).thenReturn(categories);
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk());
    }
}