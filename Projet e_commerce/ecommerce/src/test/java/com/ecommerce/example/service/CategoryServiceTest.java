package com.ecommerce.example.service;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic gadgets")
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Books")
                .description("Literature and education")
                .build();
    }

    @Test
    @DisplayName("Devrait retourner toutes les catégories sous forme de DTO")
    void shouldFindAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<CategoryDTO> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Books", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner une catégorie par son ID")
    void shouldFindById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        CategoryDTO result = categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une EntityNotFoundException si la catégorie n'existe pas")
    void shouldFindById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(99L));
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Devrait créer une nouvelle catégorie")
    void shouldCreateCategory() {
        CategoryDTO inputDto = CategoryDTO.builder()
                .name("Clothing")
                .description("Fashion items")
                .build();

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        CategoryDTO result = categoryService.create(inputDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Clothing", result.getName());
        assertEquals("Fashion items", result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour une catégorie existante")
    void shouldUpdateCategory_Success() {
        CategoryDTO updateDto = CategoryDTO.builder()
                .name("Electronics & Gadgets")
                .description("Updated description")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDTO result = categoryService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Electronics & Gadgets", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de l'update si la catégorie n'existe pas")
    void shouldUpdateCategory_NotFound() {
        CategoryDTO updateDto = CategoryDTO.builder()
                .name("Unknown")
                .build();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(99L, updateDto));
        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Devrait supprimer une catégorie existante")
    void shouldDeleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une exception lors du delete si la catégorie n'existe pas")
    void shouldDeleteCategory_NotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(99L));
        verify(categoryRepository, times(1)).existsById(99L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
