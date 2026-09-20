package com.ecommerce.example.service;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Informatique")
                .description("Produits informatiques")
                .build();

        product1 = Product.builder()
                .id(1L)
                .name("Laptop Lenovo")
                .description("PC portable performant")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .category(category)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Wireless Mouse")
                .description("Souris sans fil ergonomique")
                .price(new BigDecimal("29.99"))
                .stock(50)
                .category(category)
                .build();
    }

    @Test
    @DisplayName("Devrait retourner la liste de tous les produits sous forme de DTO")
    void shouldGetAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        // Act
        List<ProductDTO> result = productService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop Lenovo", result.get(0).getName());
        assertEquals("Wireless Mouse", result.get(1).getName());
        assertEquals("Informatique", result.get(0).getCategoryName());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner un produit par son ID")
    void shouldFindById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act
        ProductDTO result = productService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop Lenovo", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        assertEquals("Informatique", result.getCategoryName());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si le produit n'existe pas par ID")
    void shouldFindById_NotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.findById(99L)
        );
        assertTrue(exception.getMessage().contains("Product not found with id: 99"));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Devrait retourner les produits par ID de catégorie")
    void shouldFindByCategory() {
        // Arrange
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(product1, product2));

        // Act
        List<ProductDTO> result = productService.findByCategory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    @DisplayName("Devrait rechercher les produits par mot-clé")
    void shouldSearchProducts() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("Lenovo")).thenReturn(List.of(product1));

        // Act
        List<ProductDTO> result = productService.search("Lenovo");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop Lenovo", result.get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Lenovo");
    }

    @Test
    @DisplayName("Devrait créer un nouveau produit avec succès")
    void shouldCreateProduct_Success() {
        // Arrange
        ProductDTO inputDto = ProductDTO.builder()
                .name("Clavier Mécanique")
                .description("Clavier RGB")
                .price(new BigDecimal("79.99"))
                .stock(15)
                .categoryId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        // Act
        ProductDTO result = productService.create(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Clavier Mécanique", result.getName());
        assertEquals("Informatique", result.getCategoryName());

        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Devrait lever une exception si la catégorie n'existe pas lors de la création")
    void shouldCreateProduct_CategoryNotFound() {
        // Arrange
        ProductDTO inputDto = ProductDTO.builder()
                .name("Clavier Mécanique")
                .categoryId(99L)
                .build();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> productService.create(inputDto));
        verify(categoryRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour un produit existant")
    void shouldUpdateProduct_Success() {
        // Arrange
        ProductDTO updateDto = ProductDTO.builder()
                .name("Laptop Lenovo Pro")
                .description("Version mise à jour")
                .price(new BigDecimal("1099.99"))
                .stock(8)
                .categoryId(1L)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductDTO result = productService.update(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop Lenovo Pro", result.getName());
        assertEquals("Version mise à jour", result.getDescription());
        assertEquals(new BigDecimal("1099.99"), result.getPrice());
        assertEquals(8, result.getStock());

        verify(productRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Devrait supprimer un produit existant par ID")
    void shouldDeleteProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.delete(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception lors de la suppression si le produit n'existe pas")
    void shouldDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> productService.delete(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}