package com.ecommerce.example.service;

import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderItemService;
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
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order order;
    private Product product;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    void setUp() {
        order = Order.builder().id(10L).build();

        product = Product.builder()
                .id(100L)
                .name("Laptop Lenovo")
                .price(new BigDecimal("2500.00"))
                .build();

        orderItem1 = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("2500.00"))
                .build();

        orderItem2 = OrderItem.builder()
                .id(2L)
                .order(order)
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("5000.00"))
                .build();
    }

    @Test
    @DisplayName("Devrait retourner tous les articles de commande sous forme de DTO")
    void shouldFindAllOrderItems() {
        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem1, orderItem2));

        List<OrderItemDTO> result = orderItemService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getProductId());
        assertEquals("Laptop Lenovo", result.get(0).getProductName());
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner les articles par ID de commande")
    void shouldFindByOrder() {
        when(orderItemRepository.findByOrderId(10L)).thenReturn(List.of(orderItem1));

        List<OrderItemDTO> result = orderItemService.findByOrder(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(orderItemRepository, times(1)).findByOrderId(10L);
    }

    @Test
    @DisplayName("Devrait retourner un article par son ID")
    void shouldFindById_Success() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem1));

        OrderItemDTO result = orderItemService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getQuantity());
        assertEquals(new BigDecimal("2500.00"), result.getUnitPrice());
        verify(orderItemRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une EntityNotFoundException si l'article n'existe pas")
    void shouldFindById_NotFound() {
        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.findById(99L));
        verify(orderItemRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Devrait créer un nouvel article de commande avec succès")
    void shouldCreateOrderItem_Success() {
        OrderItemDTO inputDto = OrderItemDTO.builder()
                .orderId(10L)
                .productId(100L)
                .quantity(3)
                .unitPrice(new BigDecimal("2500.00"))
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        OrderItemDTO result = orderItemService.create(inputDto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(3, result.getQuantity());
        assertEquals(10L, result.getOrderId());
        assertEquals(100L, result.getProductId());
        verify(orderRepository, times(1)).findById(10L);
        verify(productRepository, times(1)).findById(100L);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la création si la commande n'existe pas")
    void shouldCreateOrderItem_OrderNotFound() {
        OrderItemDTO inputDto = OrderItemDTO.builder()
                .orderId(99L)
                .productId(100L)
                .build();

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.create(inputDto));
        verify(orderRepository, times(1)).findById(99L);
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la création si le produit n'existe pas")
    void shouldCreateOrderItem_ProductNotFound() {
        OrderItemDTO inputDto = OrderItemDTO.builder()
                .orderId(10L)
                .productId(999L)
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.create(inputDto));
        verify(productRepository, times(1)).findById(999L);
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Devrait supprimer un article existant")
    void shouldDeleteOrderItem_Success() {
        when(orderItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderItemRepository).deleteById(1L);

        orderItemService.delete(1L);

        verify(orderItemRepository, times(1)).existsById(1L);
        verify(orderItemRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait lancer une exception lors de la suppression si l'article n'existe pas")
    void shouldDeleteOrderItem_NotFound() {
        when(orderItemRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderItemService.delete(99L));
        verify(orderItemRepository, times(1)).existsById(99L);
        verify(orderItemRepository, never()).deleteById(anyLong());
    }
}
