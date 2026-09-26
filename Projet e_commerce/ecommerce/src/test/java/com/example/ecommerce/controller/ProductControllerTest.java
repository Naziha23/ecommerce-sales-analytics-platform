package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    @DisplayName("Devrait retourner tous les produits")
    void shouldGetAllProducts() {
        ProductDTO dto = ProductDTO.builder().id(1L).name("Laptop").price(new BigDecimal("1000")).build();
        when(productService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Laptop", response.getBody().get(0).getName());
    }

    @Test
    @DisplayName("Devrait créer un nouveau produit avec statut 201")
    void shouldCreateProduct() {
        ProductDTO input = ProductDTO.builder().name("Mouse").build();
        ProductDTO output = ProductDTO.builder().id(2L).name("Mouse").build();
        when(productService.create(any(ProductDTO.class))).thenReturn(output);

        ResponseEntity<ProductDTO> response = productController.createProduct(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    @DisplayName("Devrait retourner un produit par son id")
    void shouldGetProductById() {
        ProductDTO dto = ProductDTO.builder().id(1L).name("Keyboard").build();
        when(productService.findById(1L)).thenReturn(dto);

        ResponseEntity<ProductDTO> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Keyboard", response.getBody().getName());
    }

    @Test
    @DisplayName("Devrait supprimer un produit et retourner 204")
    void shouldDeleteProduct() {
        doNothing().when(productService).delete(1L);

        ResponseEntity<Void> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).delete(1L);
    }
}
