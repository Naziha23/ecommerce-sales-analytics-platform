package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    @DisplayName("Devrait retourner toutes les catégories avec le statut 200")
    void shouldGetAllCategories() {
        CategoryDTO dto = CategoryDTO.builder().id(1L).name("Test Category").description("Desc").build();
        when(categoryService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<CategoryDTO>> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Category", response.getBody().get(0).getName());
    }

    @Test
    @DisplayName("Devrait créer une nouvelle catégorie avec le statut 201")
    void shouldCreateCategory() {
        CategoryDTO input = CategoryDTO.builder().name("New Category").description("New Desc").build();
        CategoryDTO saved = CategoryDTO.builder().id(2L).name("New Category").description("New Desc").build();
        when(categoryService.create(any(CategoryDTO.class))).thenReturn(saved);

        ResponseEntity<CategoryDTO> response = categoryController.createCategory(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("New Category", response.getBody().getName());
    }

    @Test
    @DisplayName("Devrait retourner une catégorie par son id")
    void shouldGetCategoryById() {
        CategoryDTO dto = CategoryDTO.builder().id(1L).name("Found").build();
        when(categoryService.findById(1L)).thenReturn(dto);

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Found", response.getBody().getName());
    }

    @Test
    @DisplayName("Devrait supprimer une catégorie et retourner 204")
    void shouldDeleteCategory() {
        doNothing().when(categoryService).delete(1L);

        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService).delete(1L);
    }
}
