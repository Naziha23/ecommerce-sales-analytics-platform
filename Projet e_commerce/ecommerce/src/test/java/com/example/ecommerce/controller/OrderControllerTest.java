package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.service.OrderService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    @DisplayName("Devrait retourner toutes les commandes")
    void shouldGetAllOrders() {
        OrderDTO dto = OrderDTO.builder().id(1L).customerId(1L).status(OrderStatus.PENDING).build();
        when(orderService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<OrderDTO>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.PENDING, response.getBody().get(0).getStatus());
    }

    @Test
    @DisplayName("Devrait créer une nouvelle commande avec statut 201")
    void shouldCreateOrder() {
        OrderDTO input = OrderDTO.builder().customerId(1L).build();
        OrderDTO output = OrderDTO.builder().id(2L).customerId(1L).status(OrderStatus.PENDING).build();
        when(orderService.create(any(OrderDTO.class))).thenReturn(output);

        ResponseEntity<OrderDTO> response = orderController.createOrder(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    @DisplayName("Devrait retourner une commande par son id")
    void shouldGetOrderById() {
        OrderDTO dto = OrderDTO.builder().id(1L).status(OrderStatus.SHIPPED).build();
        when(orderService.findById(1L)).thenReturn(dto);

        ResponseEntity<OrderDTO> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.SHIPPED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Devrait supprimer une commande et retourner 204")
    void shouldDeleteOrder() {
        doNothing().when(orderService).delete(1L);

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService).delete(1L);
    }
}
