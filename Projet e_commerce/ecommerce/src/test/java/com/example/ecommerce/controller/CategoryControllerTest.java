package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait retourner toutes les catégories avec le statut 200")
    public void shouldGetAllCategories() throws Exception {
        CategoryDTO category = CategoryDTO.builder().id(1L).name("Test Category").description("Desc").build();
        Mockito.when(categoryService.findAll()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    @DisplayName("Devrait créer une nouvelle catégorie avec le statut 201")
    public void shouldCreateCategory() throws Exception {
        CategoryDTO inputDto = CategoryDTO.builder().name("New Category").description("New Desc").build();
        CategoryDTO returnedDto = CategoryDTO.builder().id(2L).name("New Category").description("New Desc").build();
        
        Mockito.when(categoryService.create(any(CategoryDTO.class))).thenReturn(returnedDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("New Category"));
    }
}
